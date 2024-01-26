//package com.example.chess_android.model;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Castling {
//    public Board board;
//    public King king;
//
//    public Castling(Board board, King king){
//        this.board  = board;
//        this.king = king;
//
//    }

//    public boolean castle(Coordinates newCoords){
//        Coordinates newRookCoords = calculateNewRookCoords(newCoords);
//        Coordinates kingCoords = board.getElementCoordinates(king);
//        Coordinates rookCoords = board.getElementCoordinates(rook);
//        if(newRookCoords != null && checkKing(newCoords)){
//            board.setCell(kingCoords.getRow(), kingCoords.getColumn(), null);
//            board.setCell(newCoords.getRow(), newCoords.getColumn(), king);
//            board.setCell(rookCoords.getRow(), rookCoords.getColumn(), null);
//            board.setCell(newRookCoords.getRow(), newRookCoords.getColumn(), rook);
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
//
//    public List<Coordinates> check(){
//        List<Coordinates> coordinates = new ArrayList<>();
//        Coordinates kingCoordinates = board.getElementCoordinates(king);
//        if(kingCondition(king.isChecked, king.isMoved)){
//            return new ArrayList<>();
//        }
//        if (checkSide(new Coordinates(kingCoordinates.getRow(), kingCoordinates.getColumn() + 2))){
//            coordinates.add(new Coordinates(kingCoordinates.getRow(), kingCoordinates.getColumn() + 2));
//        }
//        if (checkSide(new Coordinates(kingCoordinates.getRow(), kingCoordinates.getColumn() - 2))){
//            coordinates.add(new Coordinates(kingCoordinates.getRow(), kingCoordinates.getColumn() - 2));
//        }
//        return coordinates;
//    }
//
//    private boolean checkSide(Coordinates cords){
//        Coordinates startKingCords = board.getElementCoordinates(king);
//        Board copyBoard = new Board();
//        copyBoard.setFields(board.getFields());
//        int buff = getBuffer(cords.getColumn() - startKingCords.getColumn());
//        int current = startKingCords.getColumn() + buff;
//        for (int i = 0; i < 2; i++){
//            if(!copyBoard.isEmptyField(startKingCords.getRow(), current)){
//                return false;
//            }
//            copyBoard.setCell(startKingCords.getRow(), current, king);
//            MoveValidator moveValidator = new MoveValidator(board, king);
//            if (moveValidator.checkForCheck(new Coordinates(startKingCords.getRow(), current), copyBoard)){
//                return false;
//            }
//            current += buff;
//        }
//        return true;
//    }
//
//    private int getBuffer(int variance){
//        if(variance > 0){
//        return 1;
//        }
//        else {
//            return -1;
//        }
//    }
//
//    public static boolean kingCondition(boolean isChecked, boolean isMoved){
//        return isChecked || isMoved;
//    }
//
//    private Coordinates calculateNewRookCoords(Coordinates newCoords){
//        Coordinates oldRookCoords = board.getElementCoordinates(rook);
//        if (oldRookCoords.getRow() == -1 && oldRookCoords.getColumn() == -1){
//            return null;
//        }
//        Coordinates newRookCoords = new Coordinates(newCoords.getRow(), newCoords.getColumn() + getBuffer(newCoords.getColumn() - oldRookCoords.getColumn()));
//        if(rook.calculatePossibleMoves(oldRookCoords, board).contains(newRookCoords)){
//            return newRookCoords;
//        }
//        return null;
//    }
//}
