//package com.example.chess_android.model;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.constraintlayout.widget.ConstraintLayout;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Game {
//    private Figure selectedFigure;
//    private Board board;
//    public List<Figure> myFigures;
//    public King myKing;
//    private final String myColor;
//
//
//    public Game(String color){
//        board = new Board();
//        myColor = color;
//        board.setFields();
//    }
//
//    public void setMyKing() {
//        for (Figure figure : myFigures) {
//            if (figure instanceof King) {
//                myKing = (King) figure;
//                break;
//            }
//        }
//    }
//
//    public Coordinates getKingCords(Board board) {
//        return board.getElementCoordinates(this.myKing);
//    }
//
//    public void setSelectedFigure(Figure figure) {
//        this.selectedFigure = figure;
//    }
//
//    public Board getBoard() {
//        return board;
//    }
//
//    public MoveResult processCords(Coordinates coordinates) {
//        Figure figure = board.getFields().get(coordinates.getRow()).get(coordinates.getColumn());
//        if (figure != null && figure.color.equals(myColor)) {
//            setSelectedFigure(figure);
//            return new MoveResult(Status.SELECTED);
//        }
//        else {
//            if (selectedFigure != null) {
//                move(coordinates);
//                return new MoveResult(Status.MOVED);
//            }
//            else {
//                return new MoveResult(Status.REJECTED);
//            }
//        }
//    }
//
//    private MoveResult move(Coordinates coordinates) {
//        if(canMove(coordinates, board, selectedFigure)){
//            Coordinates selectedFigureCoordinates = board.getElementCoordinates(selectedFigure);
//            Figure nextCell = board.getFields().get(coordinates.getRow()).get(coordinates.getColumn());
//            board.setCell(selectedFigureCoordinates.getRow(), selectedFigureCoordinates.getColumn(), null);
//            board.setCell(coordinates.getRow(), coordinates.getColumn(), selectedFigure);
//            selectedFigure.isMoved = true;
//            //movedFigure = selectedFigure;
//            selectedFigure = null;
//            myKing.isChecked = false;
//            return new MoveResult(Status.MOVED);
//        }
//        else {
//            return new MoveResult(Status.REJECTED);
//        }
//    }
//
//    private List<Figure> getEnemyFigures(Board board) {
//        String needColor;
//        if (myColor.equals("white")) {
//            needColor = "black";
//        } else {
//            needColor = "white";
//        }
//        return board.getFiguresByColor(needColor);
//    }
//
//    public boolean checkForCheck(Coordinates figureCoordinates, Board board) {
//        List<Coordinates> totalEnemyMoves = new ArrayList<>();
//        for (Figure enemyFigure : getEnemyFigures(board)) {
//            List<Coordinates> moves = enemyFigure.calculatePossibleMoves(board.getElementCoordinates(enemyFigure), board);
//            totalEnemyMoves.addAll(moves);
//        }
//        return findMatch(figureCoordinates, totalEnemyMoves);
//    }
//
//    private boolean findMatch(Coordinates kingCords, List<Coordinates> enemyFiguresMoves) {
//        for (Coordinates move : enemyFiguresMoves) {
//            if (move.equals(kingCords)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public boolean canMove(Coordinates coordinatesToMove, Board board, Figure figure) {
//        Coordinates selfCoordinates = board.getElementCoordinates(figure);
//        List<Coordinates> result = figure.calculatePossibleMoves(selfCoordinates, board);
//        System.out.println(result);
//        for (Coordinates coordinates : result) {
//            if (coordinates.equals(coordinatesToMove)) {
//                Board copyBoard = new Board();
//                copyBoard.setFields(board.getFields());
//                copyBoard.setCell(selfCoordinates.getRow(), selfCoordinates.getColumn(), null);
//                copyBoard.setCell(coordinatesToMove.getRow(), coordinatesToMove.getColumn(), figure);
//                return !checkForCheck(getKingCords(copyBoard), copyBoard);
//            }
//        }
//        return false;
//    }
//
//    public Figure getSelectedFigure() {
//        return selectedFigure;
//    }
//
//    public void placePieces(ConstraintLayout root) {
//        Figure[] whiteFigures = {new Rook("white", root.getContext()),
//                new Knight("white", root.getContext()),
//                new Bishop("white", root.getContext()),
//                new Queen("white", root.getContext()),
//                new King("white", root.getContext()),
//                new Bishop("white", root.getContext()),
//                new Knight("white", root.getContext()),
//                new Rook("white", root.getContext())};
//
//        Figure[] blackFigures = {new Rook("black", root.getContext()),
//                new Knight("black", root.getContext()),
//                new Bishop("black", root.getContext()),
//                new Queen("black", root.getContext()),
//                new King("black", root.getContext()),
//                new Bishop("black", root.getContext()),
//                new Knight("black", root.getContext()),
//                new Rook("black", root.getContext())};
//        int white;
//        int black;
//        int whiteBuffer;
//        int blackBuffer;
//        if (myColor.equals("white")){
//            white = 7;
//            black = 0;
//            whiteBuffer = -1;
//            blackBuffer = 1;
//        }
//        else {
//            white = 0;
//            black = 7;
//            whiteBuffer = 1;
//            blackBuffer = -1;
//        }
//
//        for (int i = 0; i < whiteFigures.length; i++) {
//            Figure currentFig = whiteFigures[i];
//            Pawn whitePawn = new Pawn("white", -1, root.getContext());
//            board.setCell(white, i, currentFig);
//            board.setCell(white + whiteBuffer, i, whitePawn);
//            root.addView(currentFig.getMyImage());
//            root.addView(whitePawn.getMyImage());
//        }
//        for (int i = 0; i < blackFigures.length; i++) {
//            Figure currentFig = blackFigures[i];
//            Pawn blackPawn = new Pawn("black", 1, root.getContext());
//            board.setCell(black, i, currentFig);
//            board.setCell(black + blackBuffer, i, blackPawn);
//            root.addView(currentFig.getMyImage());
//            root.addView(blackPawn.getMyImage());
//        }
//
//        myFigures = board.getFiguresByColor(myColor);
//        setMyKing();
//    }
//}
