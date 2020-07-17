package acs.boundaries.sub;

public class Element {

    private ElementId elementId;

    public Element() {
        this.elementId = new ElementId();
    }

    public Element(ElementId elementId) {
        this.elementId = elementId;
    }

    public ElementId getElementId() {
        return elementId;
    }

    public void setElementId(ElementId elementId) {
        this.elementId = elementId;
    }

    @Override
    public String toString() {
        return "Element [elementId=" + elementId + "]";
    }
}