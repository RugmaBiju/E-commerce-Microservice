package main.java.com.ecom.model;

public abstract class Entity {
    protected int id;

    public Entity() {}
    public Entity(int id) { this.id = id; }

    public int getId()        { return id; }
    public void setId(int id) { this.id = id; }

    // Every subclass MUST implement this
    public abstract String getDisplayInfo();

    @Override
    public String toString() { return getDisplayInfo(); }
}

