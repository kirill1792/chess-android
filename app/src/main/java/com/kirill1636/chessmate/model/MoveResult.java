package com.kirill1636.chessmate.model;

import java.util.List;

public class MoveResult {
    public Status status;
    public List<Coordinates> coordinates;
    public Coordinates moveFrom;
    public Coordinates moveTo;

    public MoveResult(Status status){
        this.status = status;
    }

    public MoveResult(Status status, List<Coordinates> coordinates){
        this.status = status;
        this.coordinates = coordinates;
    }
    public MoveResult(Status status, Coordinates moveFrom, Coordinates moveTo){
        this.status = status;
        this.coordinates = coordinates;
        this.moveFrom = moveFrom;
        this.moveTo = moveTo;
    }
}
