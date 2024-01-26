package com.kirill1636.chessmate.model;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Figure selectedFigure;
    private Board board;
    public List<Figure> myFigures;
    public King myKing;
    private final String myColor;
    public Coordinates pawnCords;
    public Coordinates pawnMoveToCords;
    private String opponentName;
    private boolean isMyMove;
    private int myId;
    private int gameId;
    private Coordinates enPassantFrom;
    private Coordinates enPassantTo;

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getMyColor() {
        return myColor;
    }

    public int getMyId() {
        return myId;
    }

    public void setMyId(int myId) {
        this.myId = myId;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public boolean isMyMove() {
        return isMyMove;
    }

    public void setMyMove(boolean myMove) {
        isMyMove = myMove;
    }

    public Game(String color){
        board = new Board();
        myColor = color;
        board.setFields();
        setMyMove(myColor.equals("white"));
    }

    public void setMyKing() {
        for (Figure figure : myFigures) {
            if (figure instanceof King) {
                myKing = (King) figure;
                break;
            }
        }
    }

    public Coordinates getKingCords(Board board) {
        return board.getElementCoordinates(this.myKing);
    }

    public void setSelectedFigure(Figure figure) {
        this.selectedFigure = figure;
    }

    public Board getBoard() {
        return board;
    }

    public MoveResult processCords(Coordinates coordinates) {
        Figure figure = board.getFields().get(coordinates.getRow()).get(coordinates.getColumn());
        if (figure != null && figure.color.equals(myColor)) {
                setSelectedFigure(figure);
                MoveValidator validator = new MoveValidator(board, selectedFigure, enPassantFrom, enPassantTo);
                List<Coordinates> moves = validator.findFinalMoves();
                return new MoveResult(Status.SELECTED, moves);
        }
        else {
            if (selectedFigure != null) {
                return move(coordinates);
            }
            else {
                return new MoveResult(Status.REJECTED, new ArrayList<>());
            }
        }
    }

    private MoveResult move(Coordinates coordinates) {
        MoveValidator validator = new MoveValidator(board, selectedFigure, enPassantFrom, enPassantTo);
        List<Coordinates> moves = validator.findFinalMoves();
        if(moves.contains(coordinates)){
            Rook rook = validator.dictionary.get(coordinates);
            Coordinates selectedFigureCoordinates = board.getElementCoordinates(selectedFigure);
            if (enPassantFrom != null && enPassantTo != null && selectedFigure instanceof Pawn) {
                Pawn pawn = (Pawn) board.getFields().get(enPassantTo.getRow()).get(enPassantTo.getColumn());
                if (coordinates.equals(new Coordinates(enPassantTo.getRow() - pawn.direction, enPassantTo.getColumn()))) {
                    board.setCell(enPassantTo.getRow(), enPassantTo.getColumn(), null);
                    board.setCell(enPassantTo.getRow() - pawn.direction, enPassantTo.getColumn(), pawn);
                }
            }
            enPassantFrom = null;
            enPassantTo = null;
            if (selectedFigure instanceof Pawn && Math.abs(coordinates.getRow() - selectedFigureCoordinates.getRow()) == 2){
                enPassantFrom = selectedFigureCoordinates;
                enPassantTo = coordinates;
            }
            if(rook != null) {
                Coordinates kingCords = board.getElementCoordinates(selectedFigure);
                Coordinates rookCords = board.getElementCoordinates(rook);
                board.setCell(kingCords.getRow(), kingCords.getColumn(), null);
                board.setCell(coordinates.getRow(), coordinates.getColumn(), selectedFigure);
                board.setCell(rookCords.getRow(), rookCords.getColumn(), null);
                board.setCell(coordinates.getRow(), coordinates.getColumn() + defineRookPlace(kingCords.getColumn(), rookCords.getColumn()), rook);
                rook.isMoved = true;
                selectedFigure.isMoved = true;
                selectedFigure = null;
                isMyMove = false;
                return new MoveResult(Status.MOVED, selectedFigureCoordinates, coordinates);
            }

            if (coordinates.getRow() == 0 && selectedFigure instanceof Pawn){
                pawnCords = selectedFigureCoordinates;
                pawnMoveToCords = coordinates;
                return new MoveResult(Status.NEW_FIGURE, new ArrayList<>());
            }
            Figure nextCell = board.getFields().get(coordinates.getRow()).get(coordinates.getColumn());
            if(nextCell != null){
                nextCell.getMyImage().setVisibility(View.INVISIBLE);
            }
            board.setCell(selectedFigureCoordinates.getRow(), selectedFigureCoordinates.getColumn(), null);
            board.setCell(coordinates.getRow(), coordinates.getColumn(), selectedFigure);
            selectedFigure.isMoved = true;
            //movedFigure = selectedFigure;
            selectedFigure = null;
            myKing.isChecked = false;
            isMyMove = false;
            return new MoveResult(Status.MOVED, selectedFigureCoordinates, coordinates);
        }
        else {
            return new MoveResult(Status.REJECTED, moves);
        }
    }

    public NewFigureType moveAndPlaceNewFigure(Class<Figure> figureType, ConstraintLayout root){
        Figure newFig = null;
        NewFigureType newFigureType = null;
        if(figureType.equals(Queen.class)) {
            newFig = new Queen(myColor, root.getContext());
            newFigureType = NewFigureType.QUEEN;
        }
        else if (figureType.equals(Rook.class)) {
            newFig = new Rook(myColor, root.getContext());
            newFigureType = NewFigureType.ROOK;
        }
        else if (figureType.equals(Bishop.class)) {
            newFig = new Bishop(myColor, root.getContext());
            newFigureType = NewFigureType.BISHOP;
        }
        else if (figureType.equals(Knight.class)) {
            newFig = new Knight(myColor, root.getContext());
            newFigureType = NewFigureType.KNIGHT;
        }
        Figure figure = board.getFields().get(pawnCords.getRow()).get(pawnCords.getColumn());
        board.setCell(pawnCords.getRow(), pawnCords.getColumn(), null);
        myFigures.remove(figure);
        figure.getMyImage().setVisibility(View.INVISIBLE);
        if(board.getFields().get(pawnMoveToCords.getRow()).get(pawnMoveToCords.getColumn()) != null){
            board.getFields().get(pawnMoveToCords.getRow()).get(pawnMoveToCords.getColumn()).getMyImage().setVisibility(View.INVISIBLE);
        }
        board.setCell(pawnMoveToCords.getRow(), pawnMoveToCords.getColumn(), newFig);
        myFigures.add(newFig);
        root.addView(newFig.getMyImage());
        selectedFigure = null;
        myKing.isChecked = false;
        isMyMove = false;
        return newFigureType;
    }

    public Figure getSelectedFigure() {
        return selectedFigure;
    }

    private int defineRookPlace(int kingColumn, int rookColumn) {
        if (kingColumn - rookColumn < 0){
            return -1;
        }
        else {
            return 1;
        }
    }

    public void placePieces(ConstraintLayout root) {
        Figure[] whiteFigures = {new Rook("white", root.getContext()),
                new Knight("white", root.getContext()),
                new Bishop("white", root.getContext()),
                new Queen("white", root.getContext()),
                new King("white", root.getContext()),
                new Bishop("white", root.getContext()),
                new Knight("white", root.getContext()),
                new Rook("white", root.getContext())};

        Figure[] blackFigures = {new Rook("black", root.getContext()),
                new Knight("black", root.getContext()),
                new Bishop("black", root.getContext()),
                new Queen("black", root.getContext()),
                new King("black", root.getContext()),
                new Bishop("black", root.getContext()),
                new Knight("black", root.getContext()),
                new Rook("black", root.getContext())};

        for (int i = 0; i < whiteFigures.length; i++) {
            Figure currentFig = whiteFigures[i];
            Pawn whitePawn = new Pawn("white", -1, root.getContext());
            board.setCell(7, i, currentFig);
            board.setCell(6, i, whitePawn);
            root.addView(currentFig.getMyImage());
            root.addView(whitePawn.getMyImage());
        }
        for (int i = 0; i < blackFigures.length; i++) {
            Figure currentFig = blackFigures[i];
            Pawn blackPawn = new Pawn("black", 1, root.getContext());
            board.setCell(0, i, currentFig);
            board.setCell(1, i, blackPawn);
            root.addView(currentFig.getMyImage());
            root.addView(blackPawn.getMyImage());
        }

        if(myColor.equals("black")){
            board.turnBoard();
            changePawnDirection(board.getFiguresByColor("white"));
            changePawnDirection(board.getFiguresByColor("black"));
        }

        myFigures = board.getFiguresByColor(myColor);
        setMyKing();
    }

    private void changePawnDirection(List<Figure> figures) {
        for (Figure figure : figures) {
            if (figure instanceof Pawn) {
                ((Pawn) figure).changeDirection();
            }
        }
    }
    public void opponentMove(String moveFrom, String moveTo, NewFigureType figureType, String rookFrom, String rookTo, ConstraintLayout root) {
        Parser parser = new Parser(myColor);
        Figure figure = board.getFields().get(parser.parse(moveFrom).getRow()).get(parser.parse(moveFrom).getColumn());
        if (enPassantFrom != null && enPassantTo != null && figure instanceof Pawn) {
            Pawn pawn = (Pawn) board.getFields().get(enPassantTo.getRow()).get(enPassantTo.getColumn());
            if (parser.parse(moveTo).equals(new Coordinates(enPassantTo.getRow() - pawn.direction, enPassantTo.getColumn()))) {
                board.setCell(enPassantTo.getRow(), enPassantTo.getColumn(), null);
                board.setCell(enPassantTo.getRow() - pawn.direction, enPassantTo.getColumn(), pawn);
            }
        }
        Figure nextCell = board.getFields().get(parser.parse(moveTo).getRow()).get(parser.parse(moveTo).getColumn());
        board.setCell(parser.parse(moveFrom).getRow(), parser.parse(moveFrom).getColumn(), null);
        enPassantFrom = null;
        enPassantTo = null;
        if (figure instanceof Pawn && Math.abs(parser.parse(moveTo).getRow() - parser.parse(moveFrom).getRow()) == 2){
            enPassantFrom = parser.parse(moveFrom);
            enPassantTo = parser.parse(moveTo);
        }

        if (figureType != null) {
            if (figureType == NewFigureType.QUEEN) {
                figure.getMyImage().setVisibility(View.INVISIBLE);
                figure = new Queen(figure.color, root.getContext());
            } else if (figureType == NewFigureType.ROOK) {
                figure.getMyImage().setVisibility(View.INVISIBLE);
                figure = new Rook(figure.color, root.getContext());
            } else if (figureType == NewFigureType.BISHOP) {
                figure.getMyImage().setVisibility(View.INVISIBLE);
                figure = new Bishop(figure.color, root.getContext());
            } else if (figureType == NewFigureType.KNIGHT) {
                figure.getMyImage().setVisibility(View.INVISIBLE);
                figure = new Knight(figure.color, root.getContext());
            }
            root.addView(figure.getMyImage());
        }
        else if (rookFrom != null && rookTo != null){
            Figure rook = board.getFields().get(parser.parse(rookFrom).getRow()).get(parser.parse(rookFrom).getColumn());
            board.setCell(parser.parse(rookFrom).getRow(), parser.parse(rookFrom).getColumn(), null);
            board.setCell(parser.parse(rookTo).getRow(), parser.parse(rookTo).getColumn(), rook);
        }
        if(nextCell != null){
            nextCell.getMyImage().setVisibility(View.INVISIBLE);
            myFigures.remove(nextCell);
        }
        board.setCell(parser.parse(moveTo).getRow(), parser.parse(moveTo).getColumn(), figure);
        isMyMove = true;
    }
}
