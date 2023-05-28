package nl.arba.ada.client.api;

/**
 * Interface that represents an object that can hold content
 */
public interface ContentHolding {
    /**
     * Get the content of the object
     * @return The content of the object
     * @see Content
     */
    public Content getContent();
}
