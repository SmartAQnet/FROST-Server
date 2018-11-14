/*
 * Copyright (C) 2016 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.sta.path;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author jab
 */
public class ResourcePath {

    /**
     * Base root URI of the serivce.
     */
    private String serviceRootUrl;
    /**
     * The url that was used to generate this path.
     */
    private String pathUrl;
    /**
     * Flag indicating there was a $ref at the end of the path.
     */
    private boolean ref;
    /**
     * Flag indicating there was a $value at the end of the path.
     */
    private boolean value;
    /**
     * The elements in this path.
     */
    private List<ResourcePathElement> pathElements;
    /**
     * The "main" element specified by this path. This is either an Entity or an
     * EntitySet, so it might not be the last element in the path.
     */
    private ResourcePathElement mainElement;
    /**
     * The "last" element in this path that had a specified id.
     */
    private EntityPathElement identifiedElement;

    public ResourcePath() {
        pathElements = new ArrayList<>();
    }

    public ResourcePath(String serviceRootUrl, String pathUrl) {
        pathElements = new ArrayList<>();
        this.serviceRootUrl = serviceRootUrl;
        this.pathUrl = pathUrl;
    }

    public boolean isRef() {
        return ref;
    }

    public boolean isValue() {
        return value;
    }

    public List<ResourcePathElement> getPathElements() {
        return pathElements;
    }

    public ResourcePathElement getMainElement() {
        return mainElement;
    }

    public EntityType getMainElementType() {
        if (mainElement instanceof EntityPathElement) {
            EntityPathElement entityPathElement = (EntityPathElement) mainElement;
            return entityPathElement.getEntityType();
        }
        if (mainElement instanceof EntitySetPathElement) {
            EntitySetPathElement entitySetPathElement = (EntitySetPathElement) mainElement;
            return entitySetPathElement.getEntityType();
        }
        return null;
    }

    public ResourcePathElement getLastElement() {
        if (pathElements.isEmpty()) {
            return null;
        }
        return pathElements.get(pathElements.size() - 1);
    }

    public EntityPathElement getIdentifiedElement() {
        return identifiedElement;
    }

    public void setRef(boolean ref) {
        this.ref = ref;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void setPathElements(List<ResourcePathElement> pathElements) {
        this.pathElements = pathElements;
    }

    public void setMainElement(ResourcePathElement mainElementType) {
        this.mainElement = mainElementType;
    }

    public void setIdentifiedElement(EntityPathElement identifiedElement) {
        this.identifiedElement = identifiedElement;
    }

    public void addPathElement(ResourcePathElement pe, boolean isMain, boolean isIdentifier) {
        getPathElements().add(pe);
        if (isMain && pe instanceof EntityPathElement) {
            EntityPathElement epe = (EntityPathElement) pe;
            setMainElement(epe);
        }
        if (isIdentifier && pe instanceof EntityPathElement) {
            EntityPathElement epe = (EntityPathElement) pe;
            setIdentifiedElement(epe);
        }
    }

    public void compress() {
        for (int i = pathElements.size() - 1; i > 0; i--) {
            if (pathElements.get(i) instanceof EntityPathElement
                    && pathElements.get(i - 1) instanceof EntitySetPathElement) {
                EntityPathElement epe = (EntityPathElement) pathElements.get(i);
                if (epe.getId() != null) {
                    // crop path
                    setMainElement(pathElements.get(i - 1));
                    pathElements.subList(0, i - 1).clear();
                    setIdentifiedElement(epe);
                    return;
                }
            }
        }
    }

    public String getServiceRootUrl() {
        return serviceRootUrl;
    }

    public void setServiceRootUrl(String serviceRootUrl) {
        this.serviceRootUrl = serviceRootUrl;
    }

    public String getPathUrl() {
        return pathUrl;
    }

    public void setPathUrl(String pathUrl) {
        this.pathUrl = pathUrl;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceRootUrl, ref, value, pathElements, mainElement, identifiedElement);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResourcePath other = (ResourcePath) obj;
        return Objects.equals(this.serviceRootUrl, other.serviceRootUrl)
                && this.ref == other.ref
                && this.value == other.value
                && Objects.equals(this.pathElements, other.pathElements)
                && Objects.equals(this.mainElement, other.mainElement)
                && Objects.equals(this.identifiedElement, other.identifiedElement);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(serviceRootUrl);
        for (ResourcePathElement rpe : pathElements) {
            if (rpe instanceof EntityPathElement && ((EntityPathElement) rpe).getId() != null) {
                EntityPathElement epe = (EntityPathElement) rpe;
                sb.append("(").append(epe.getId().getUrl()).append(")");
            } else if (rpe instanceof CustomPropertyArrayIndex) {
                sb.append(rpe.toString());
            } else {
                sb.append("/");
                sb.append(rpe.toString());
            }
        }
        if (isRef()) {
            sb.append("/$ref");
        } else if (isValue()) {
            sb.append("/$value");
        }
        return sb.toString();
    }

}
