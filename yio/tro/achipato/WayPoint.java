package yio.tro.achipato;

import android.util.Log;

import java.util.ArrayList;

class WayPoint {
    private double x;
    private double y;
    ArrayList<WayPoint> links;
    private final WaypointGraph graph;
    WayPoint wayDirection;
    double currentDistance;
    boolean filled;
    boolean active;

    public WayPoint(WaypointGraph graph) {
        this(graph, 0, 0);
    }

    public WayPoint(WaypointGraph graph, double x, double y) {
        this.graph = graph;
        this.x = x;
        this.y = y;
        links = new ArrayList<WayPoint>();
        currentDistance = 0;
        active = true;
    }

    void deactivate() {
        active = false;
    }

    void activate() {
        active = true;
    }

    void updateLinks(){
        links = new ArrayList<WayPoint>();
        for (int i=0; i<graph.getPoints().size(); i++)
            if (graph.getPoints().get(i) != this)
                links.add(graph.getPoints().get(i));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    boolean containsLinkToPoint(WayPoint p) {
        for (int i=links.size()-1; i>=0; i--)
            if (links.get(i) == p) return true;
        return false;
    }

    void addLink(WayPoint p){
        if (p == this) return;
        if (containsLinkToPoint(p)) return;
        links.add(p);
    }

    void removeLink(WayPoint p){
        links.remove(p);
    }

    int linksListSize(){
        return links.size();
    }

    boolean isLinkedTo(WayPoint point) {
        for (int i=0; i<links.size(); i++)
            if (links.get(i) == point) return true;
        return false;
    }

    void fillAllAround(){
        filled = true;
        for (int i=0; i<links.size(); i++)
            if (links.get(i).active && !links.get(i).filled)
                links.get(i).fillAllAround();
    }

    public WaypointGraph getWaypointGraph() {
        return graph;
    }

    public ArrayList<WayPoint> getLinks() {
        return links;
    }

}
