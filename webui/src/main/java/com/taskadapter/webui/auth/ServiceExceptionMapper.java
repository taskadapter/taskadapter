package com.taskadapter.webui.auth;

import com.taskadapter.webui.Layout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.ParentLayout;

import javax.servlet.http.HttpServletResponse;

/**
 * this will be automatically registered as exception mapped by Vaadin because it implements "HasErrorParameter"
 * interface.
 */
@ParentLayout(Layout.class)
public class ServiceExceptionMapper extends ExceptionPage implements HasErrorParameter<PermissionViolationException> {

    @Override
    public int setErrorParameter( BeforeEnterEvent event,  ErrorParameter<PermissionViolationException> parameter) {
        Exception exception = parameter.getException();
        showError(VaadinIcon.LOCK, exception.getMessage());

//        switch (exception.getCode()) {
//            case ErrorCode.UNAUTHORIZED:
//                showError(VaadinIcon.LOCK, exception.getMessage());
//                break;
//            case ErrorCode.NOT_FOUND:
//                showError(VaadinIcon.MEH_O, exception.getMessage());
//                break;
//            default:
//                logger.warn("", parameter.getCaughtException());
//                showError(parameter.getCaughtException());
//                break;
//        }

        return HttpServletResponse.SC_OK;
    }
}
