package com.ipn.mx.BusquedaInformada;

import java.util.Objects;

public class Node {
    public int x;
    public int y;
    public int f;
    public int g;
    public int h;
    public Node father;

    public Node(int x, int y, int h, Node father) {
        this.x = x;
        this.y = y;
        this.h = h;
        this.father = father;
        this.g = (father != null) ? this.father.g + 1 :  0;
        this.f = g + this.h; 
    }

    public int[] getPosition() {
        return new int[]{this.x, this.y};
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
