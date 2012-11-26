package com.taskadapter.web.ui;

/**
 * Item renderer.
 * 
 * @param <S>
 *            source type.
 * @param <T>
 *            target type.
 */
public interface Renderer<S, T> {
    /**
     * Renders an item.
     * 
     * @param item
     *            item to render.
     * @return rendered item.
     */
    public T render(S item);
}
