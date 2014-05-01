package nl.trifork.spring.navigation;

/**
 * @author Quinten Krijger
 */
public interface SessionAttributeUpdater<T> {

    /**
     * Updates a session attribute
     *
     * @param attributeToUpdate attribute that should be updated
     * @param domainClass class of attribute
     * @return updated attribute
     * @throws IllegalArgumentException in case {@code domainClass} is not {@code null} and not of type T
     */
    T update(Object attributeToUpdate, Class<T> domainClass);

}
