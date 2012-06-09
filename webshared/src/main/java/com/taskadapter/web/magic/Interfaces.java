package com.taskadapter.web.magic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Interfaces converters and factories.
 * 
 * @author maxkar
 * 
 */
public final class Interfaces {

	/**
	 * Creates implementation of interface with a single method from a specified
	 * method.
	 * 
	 * @param iface
	 *            interface to use.
	 * @param object
	 *            object to call method on.
	 * @param method
	 *            method to call.
	 * @param args
	 *            additional arguments (interface arguments are added last).
	 * @return interface implementation, which calls a specified method.
	 * @throws IllegalArgumentException
	 *             if interface implementation is invalid.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromMethod(Class<T> iface, Object object,
			String method, Object... args) {
		if (!iface.isInterface())
			throw new IllegalArgumentException(
					"Target class does not denote an interface");
		final Method[] targetMethods = iface.getDeclaredMethods();
		if (targetMethods.length != 1) {
			throw new IllegalArgumentException("Target interface have "
					+ targetMethods.length
					+ " declared methods, but only 1 supported");
		}

		final List<Method> sourceMethods = getMethods(object, method);
		if (sourceMethods.size() != 1) {
			throw new IllegalArgumentException(
					"There is no unique instance of method with name " + method
							+ ", there are " + sourceMethods.size()
							+ " methods");
		}

		final Method sourceMethod = sourceMethods.get(0);
		sourceMethod.setAccessible(true);

		ensureMatches(targetMethods[0], sourceMethod, args);

		final boolean isStaticAccess = object instanceof Class;
		final Class<?> targetClass = isStaticAccess ? (Class<?>) object
				: object.getClass();
		final Object target = isStaticAccess ? null : object;

		final InvocationHandler handler = args.length == 0 ? createSimpleHandler(
				sourceMethod, target) : createComplexHandler(sourceMethod,
				target, args);
		return (T) Proxy.newProxyInstance(targetClass.getClassLoader(),
				new Class<?>[] { iface }, handler);
	}

	/**
	 * Creates a complex handler.
	 * 
	 * @param sourceMethod
	 *            source method.
	 * @param target
	 *            target object.
	 * @param extraArgs
	 *            additional args.
	 * @return invocation handler.
	 */
	private static InvocationHandler createComplexHandler(
			final Method sourceMethod, final Object target,
			final Object[] extraArgs) {
		return new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				try {
					final Object[] newArgs;
					if (args != null) {
						newArgs = new Object[extraArgs.length + args.length];
						System.arraycopy(extraArgs, 0, newArgs, 0,
								extraArgs.length);
						System.arraycopy(args, 0, newArgs, extraArgs.length,
								args.length);
					} else {
						newArgs = extraArgs;
					}
					return sourceMethod.invoke(target, newArgs);
				} catch (InvocationTargetException e) {
					throw e.getTargetException();
				}
			}
		};
	}

	/**
	 * Creates a simple invocation handler.
	 * 
	 * @param sourceMethod
	 *            method to invoke.
	 * @param target
	 *            target object.
	 * @return invocation handler.
	 */
	private static InvocationHandler createSimpleHandler(
			final Method sourceMethod, final Object target) {
		return new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				try {
					return sourceMethod.invoke(target, args);
				} catch (InvocationTargetException e) {
					throw e.getTargetException();
				}
			}
		};
	}

	/**
	 * Checks, if argument matches.
	 * 
	 * @param method
	 *            method to use.
	 * @param sourceMethod
	 *            source method to use.
	 * @param args
	 *            target args.
	 */
	private static void ensureMatches(Method method, Method sourceMethod,
			Object[] args) {
		final Class<?>[] targetArgs = method.getParameterTypes();
		final Class<?>[] sourceArgs = sourceMethod.getParameterTypes();
		if (targetArgs.length + args.length != sourceArgs.length) {
			throw new IllegalArgumentException(
					"Illegal number of parameters, expected "
							+ (targetArgs.length + args.length) + " but got "
							+ sourceArgs.length);
		}

		int sptr = 0;
		for (Object arg : args) {
			if (arg != null
					&& !sourceArgs[sptr++].isAssignableFrom(arg.getClass()))
				throw new IllegalArgumentException("Argument " + sptr
						+ " is not assignable from actual value");
		}

		for (Class<?> targetArg : targetArgs) {
			if (!sourceArgs[sptr++].isAssignableFrom(targetArg))
				throw new IllegalArgumentException("Argument " + sptr
						+ " is not assignable from interface parameter type "
						+ targetArg);
		}

		if (void.class != method.getReturnType()
				&& !method.getReturnType().isAssignableFrom(
						sourceMethod.getReturnType())) {
			throw new IllegalArgumentException("Result type "
					+ sourceMethod.getReturnType()
					+ " is not assignable to interface parameter type "
					+ method.getReturnType());
		}

		final Class<?>[] allowedThrowables = method.getExceptionTypes();
		final Class<?>[] actualThrowables = sourceMethod.getExceptionTypes();

		for (Class<?> c : actualThrowables)
			if (!supportsException(c, allowedThrowables))
				throw new IllegalArgumentException(
						"Target method have an unsupported declared exception "
								+ c);
	}

	/**
	 * Checks, if exception is supported.
	 * 
	 * @param c
	 *            exception class.
	 * @param allowedThrowables
	 *            allowed exception classes.
	 * @return <code>true</code> iff exception class belongs to supported
	 *         exception classes.
	 */
	private static boolean supportsException(Class<?> c,
			Class<?>[] allowedThrowables) {
		if (Error.class.isAssignableFrom(c)
				|| RuntimeException.class.isAssignableFrom(c))
			return true;

		for (Class<?> se : allowedThrowables)
			if (se.isAssignableFrom(c))
				return true;
		return false;
	}

	/**
	 * Returns a methods list.
	 * 
	 * @param object
	 *            object.
	 * @param method
	 *            method name to use.
	 * @return methods to use.
	 */
	private static List<Method> getMethods(Object object, String method) {
		if (object instanceof Class<?>) {
			final Method[] methods = ((Class<?>) object).getDeclaredMethods();
			final List<Method> result = new ArrayList<Method>();
			for (Method m : methods)
				if (method.equals(m.getName())
						&& Modifier.isStatic(m.getModifiers())) {
					result.add(m);
				}
			return result;
		} else {
			final Method[] methods = object.getClass().getDeclaredMethods();
			final List<Method> result = new ArrayList<Method>();
			for (Method m : methods)
				if (method.equals(m.getName())
						&& !Modifier.isStatic(m.getModifiers())) {
					result.add(m);
				}
			return result;
		}
	}
}
