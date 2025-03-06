package com.buscaminas.juego;

class Par {
    private int x;
    private int y;
    
    Par(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    int getX() {
        return x;
    }
    
    int getY() {
        return y;
    }
    
    @Override
    public boolean equals(Object o) {
    	if (o instanceof Par) {
    		Par p = (Par) o;
    		return p.x == x && p.y == y;
    	} 
    	return false;
    }
    
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
