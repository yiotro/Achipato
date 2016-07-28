package yio.tro.achipato;

import android.util.Log;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.Random;

public class WaypointGraph {
    private ArrayList<WayPoint> points;

    WaypointGraph(){
        initGraph();
    }

    void initGraph() {
        points = new ArrayList<WayPoint>();
        double a = 0.1 * Gdx.graphics.getWidth();
        double b = 0.5 * a;
        double c = 0.707 * a;
        double x = 0.05 * Gdx.graphics.getWidth(), y = 0.15 * Gdx.graphics.getHeight();
        boolean offset = false;
        while (y < 0.9 * Gdx.graphics.getHeight()) {
            while (x < Gdx.graphics.getWidth()) {
                WayPoint point = new WayPoint(this, x, y);
                stickPointByDistance(point, 0.85 * a, 1.05 * a);
                addPoint(point);
                x += a;
            }
            y += c;
            if (offset) x = 0.05 * Gdx.graphics.getWidth();
            else x = 0.05 * Gdx.graphics.getWidth() + b;
            offset = !offset;
        }
    }

    void addPoint(WayPoint p){
        points.add(p);
    }

    private void stickPoints(WayPoint p1, WayPoint p2) {
        if (p1 == null || p2 == null) return;
        p1.addLink(p2);
        p2.addLink(p1);
    }

    void stickPointByDistance(WayPoint point, double minDistance, double maxDistance) {
        WayPoint temp;
        double d;
        for (int i=points.size()-1; i>=0; i--) {
            temp = points.get(i);
            d = YioGdxGame.distance(point.getX(), point.getY(), temp.getX(), temp.getY());
            if (d > minDistance && d < maxDistance) {
                stickPoints(point, temp);
            }
        }
    }

    void deactivateSomePointsByModule(Module module) {
        WayPoint point;
        for (int i=points.size()-1; i>=0; i--) {
            point = points.get(i);
            if (YioGdxGame.distance(module.x, module.y, point.getX(), point.getY()) < module.afterConstructionVisibilityRange) {
                point.deactivate();
                module.deactivatedWayPoints.add(point);
            }
        }
    }

    void deactivateSomePointsByObstacle(Bubble obstacle) {
        WayPoint point;
        float offset = 0.5f * GameView.moduleSize;
        for (int i=points.size()-1; i>=0; i--) {
            point = points.get(i);
            if (YioGdxGame.distance(obstacle.x, obstacle.y, point.getX(), point.getY()) < obstacle.r + offset) {
                point.deactivate();
            }
        }
    }

    void deactivateUnreachablePoints() {
        WayPoint startPoint = findNearestPoint(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (startPoint == null || !startPoint.active) {
            for (int i=points.size()-1; i>=0; i--) points.get(i).deactivate();
            return;
        }
        prepareAllPoints();
        startPoint.fillAllAround();
        WayPoint point;
        for (int i=points.size()-1; i>=0; i--) {
            point = points.get(i);
            if (point.active && !point.filled) {
                point.deactivate();
            }
        }
    }

    void activateAllPoints() {
        for (int i=points.size()-1; i>=0; i--) points.get(i).activate();
    }

    int howManyActivePoints() {
        int c = 0;
        for (int i=points.size()-1; i>=0; i--)
            if (points.get(i).active) c++;
        return c;
    }

    public ArrayList<WayPoint> getPoints() {
        return points;
    }

    public static double distanceBetweenPoints(WayPoint point1, WayPoint point2) {
        return YioGdxGame.distance(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    double distanceFromPointToCoor(WayPoint point, double x, double y) {
        return YioGdxGame.distance(point.getX(), point.getY(), x, y);
    }

    ArrayList<WayPoint> findWayModern(WayPoint start, WayPoint finish) {
        if (YioGdxGame.distance(start.getX(), start.getY(), finish.getX(), finish.getY()) < 3 * GameView.moduleSize) {
            ArrayList<WayPoint> result = new ArrayList<WayPoint>();
            result.add(finish);
            return result;
        }
        ArrayList<WayPoint> result = findWay(findNearestPoint(start.getX(), start.getY()), findNearestPoint(finish.getX(), finish.getY()));
        if (result == null) {
            result = new ArrayList<WayPoint>();
            result.add(start);
        }
        if (result.size() > 1) {
            result.remove(result.size()-1);
        }
        result.add(finish);
        return result;
    }

    WayPoint findNearestPoint(double x, double y){ //знаходження найближчої точки
        if (points == null || points.size()<2) return null; //необхідні умови
        WayPoint result = null; //почнемо перебирати з першого елементу
        double current_distance = Gdx.graphics.getWidth() + Gdx.graphics.getHeight(); //відстань до точки
        for (int i=0; i<points.size(); i++)
            if (points.get(i).active && distanceFromPointToCoor(points.get(i), x, y) < current_distance){
                current_distance = distanceFromPointToCoor(points.get(i), x, y); //перерахувати відстань
                result = points.get(i); //замінити точку
            }
        return result; //повернути точку
    }

    void clear(){
        points = new ArrayList<WayPoint>();
    }

    void deletePoint(WayPoint p){
        for (int i=0; i<points.size(); i++)
            if (points.get(i) != p)
                points.get(i).removeLink(p);
        points.remove(p);
    }

    void prepareAllPoints(){
        for (int i=0; i<points.size(); i++) points.get(i).filled = false;
    }

    boolean isLinked(){
        if (points.size() < 2) return true; //якщо менше двох точок то граф звязний
        prepareAllPoints(); //підготовити всі точки графу
        points.get(0).fillAllAround(); //викликаєтся рекурсивний метод точки який "заповнює" цю
        boolean answer = true;                 //точку і викликає цей метод для всіх "незаповнених" точок навколо
        for (int i=0; i<points.size(); i++)        //тепер якщо не всі точки заповнені то граф незвязний
            if (!points.get(i).filled)
                answer = false;
        return answer;
    }

    private void initStartByPoints(WayPoint start, ArrayList<WayPoint> list){
        start.filled = true;
        WayPoint link;
        for (int i=0; i<start.getLinks().size(); i++){
            link = start.getLinks().get(i);
            if (!link.active) continue;
            link.wayDirection = link;
            link.filled = true;
            list.add(link);
        }
    }

    private void propagatePointByPoints(WayPoint point, ArrayList<WayPoint> currentList){
        WayPoint link;
        for (int i=0; i<point.getLinks().size(); i++){
            link = point.getLinks().get(i);
            if (link.filled) continue;
            if (!link.active) continue;
            link.filled = true;
            link.wayDirection = point.wayDirection;
            currentList.add(link);
        }
        currentList.remove(point);
    }

    private WayPoint findFirstStepByPoints(WayPoint start, WayPoint end){
        ArrayList<WayPoint> currentList = new ArrayList<WayPoint>();
        prepareAllPoints();
        initStartByPoints(start, currentList);
        while (!end.filled && currentList.size() > 0){
            propagatePointByPoints(currentList.get(0), currentList);
        }
        return end.wayDirection;
    }

    ArrayList<WayPoint> findWay(WayPoint start, WayPoint end){
        ArrayList<WayPoint> way = new ArrayList<WayPoint>(); //створюється шлях
        way.add(start); //до шляху додається точка старту
        WayPoint currentPoint = start; //поточна точка, яка поступово проходить весь шлях
        while(currentPoint != end){ //доки поточна точка не дійшла до кінця
            WayPoint dir; //напрямок
            dir = findFirstStepByPoints(currentPoint, end);
            if (dir == null) return null;
            currentPoint = dir; //присвоїти поточній точці значення напрямку знайденого вище
            way.add(dir); //додати знайдений напрямок до шляху
        }
        return way; //шлях це результат роботи методу який ми повертаємо
    }
}
