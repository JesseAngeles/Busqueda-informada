package com.ipn.mx.BusquedaInformada;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CollectorController {

    private int x_current;
    private int y_current;

    private boolean hasMineral;
    private boolean playMode;

    private int x_nearShip;
    private int y_nearShip;

    private GraphicInterface graphic;
    private int[][] board;

    /*
        0 -> Empty
        1 -> Ship
        2 -> Collector
        3 -> Mineral
        4 -> Obstacle
       -x -> Crumb
     */
    private ArrayList<int[]> shipsPosition;

    public CollectorController(GraphicInterface graphic, int[] currentPosition, ArrayList<int[]> shipsPosition) {
        this.x_current = currentPosition[0];
        this.y_current = currentPosition[1];

        this.graphic = graphic;
        this.shipsPosition = shipsPosition;
        this.board = graphic.getBoardGame();

        this.hasMineral = false;
        this.playMode = true;

        Thread thread = new Thread(task);

        try {
            Thread.sleep((long) (Math.random() * 1000));
        } catch (InterruptedException ex) {
            Logger.getLogger(CollectorController.class.getName()).log(Level.SEVERE, null, ex);
        }

        thread.start();
    }

    Runnable task = () -> {

        ArrayList<int[]> nearPositions;
        ArrayList<int[]> nearEmptyPositions;
        ArrayList<int[]> nearMineralPositions;
        ArrayList<int[]> nearShipPositions;
        ArrayList<int[]> nearCrumbPositions;
        ArrayList<int[]> nearCrumbFullPosition;

        ArrayList<int[]> rute = new ArrayList<>();

        int[] lastPosition = new int[2];

        int[] nearShip = getNearShip();
        this.x_nearShip = nearShip[0];
        this.y_nearShip = nearShip[1];

        int countPosition = 0;
        int asignValue = 0;
        boolean creatingRoute = false;

        while (this.playMode) {
            try {
                nearEmptyPositions = new ArrayList<>();
                nearMineralPositions = new ArrayList<>();
                nearShipPositions = new ArrayList<>();
                nearCrumbPositions = new ArrayList<>();

                lastPosition[0] = this.x_current;
                lastPosition[1] = this.y_current;

                // SE RECOGIO UN MINERAL
                if (this.hasMineral) {
                    Thread.sleep(500);

                    //Obtenemos los vecinos del buscador
                    nearPositions = getNearpositions(x_current, y_current);
                    for (int[] nearPosition : nearPositions) {
                        switch (moveAvailable(nearPosition)) {
                            case 0 -> {
                                nearEmptyPositions.add(nearPosition);               // Obtenemos las posiciones vacias
                            }
                            case 1 -> {
                                nearShipPositions.add(nearPosition);
                            }
                            default -> {
                                if (moveAvailable(nearPosition) < 0) {
                                    nearCrumbPositions.add(nearPosition);           // Obtenemos las posiciones con migajas
                                }
                            }
                        }
                    }

                    //PRIORIDAD DE COMPORTAMIENTO
                    if (!nearShipPositions.isEmpty()) {                             //Hay una nave cerca         
                        this.hasMineral = false;
                        creatingRoute = false;
                    } else if (!nearCrumbPositions.isEmpty() && !creatingRoute) {   // Seguir la migaja mas grande
                        PriorityQueue<int[]> nearCrumb = new PriorityQueue<>(Comparator.comparingInt((int[] n) -> n[2]).reversed());
                        for (int[] pos : nearCrumbPositions) {
                            nearCrumb.add(new int[]{pos[0], pos[1], this.board[pos[0]][pos[1]]});
                        }
                        nearCrumbPositions = new ArrayList<>();
                        nearCrumbPositions.add(nearCrumb.peek());

                        // Se mueve al mas pequeño
                        move(nearCrumbPositions, nearCrumb.peek()[2]);
                        asignValue = nearCrumb.peek()[2];
                    } else if (!nearEmptyPositions.isEmpty()) {                     // Crea camino con A*
                        // CREAR EL CAMINO DE MIGAJAS
                        creatingRoute = true;

                        if (rute.isEmpty()) { // Generamos ruta si no existe
                            rute = pathFinder(new int[]{this.x_current, this.y_current}, new int[]{this.x_nearShip, this.y_nearShip});

                            countPosition = -1 * rute.size();
                            rute.remove(0);
                            Collections.reverse(rute);
                            rute.remove(0);
                        }

                        ArrayList<int[]> nextMove = new ArrayList<>();
                        nextMove.add(rute.get(0));
                        rute.remove(0);
                        move(nextMove, 5);

                        asignValue = countPosition;
                    }

                    // Actualiza la interfaz
                    graphic.setBoard(lastPosition, new int[]{this.x_current, this.y_current}, hasMineral, asignValue);
                    this.board = graphic.getBoardGame();

                    // NO LLEVA MINERAL
                } else {
                    // Encontrar mineral
                    // Encontrar camino de migas
                    // Mover random
                    Thread.sleep(750);
                    nearPositions = getNearpositions(this.x_current, this.y_current);
                    for (int[] nearPosition : nearPositions) {
                        switch (moveAvailable(nearPosition)) {
                            case 0 ->
                                nearEmptyPositions.add(nearPosition);
                            case 3 ->
                                nearMineralPositions.add(nearPosition);
                            default -> {
                                if (moveAvailable(nearPosition) < 0) {
                                    nearCrumbPositions.add(nearPosition);
                                }
                            }
                        }
                    }

                    // PRIORIDAD DE COMPORTAMIENTO
                    if (!nearMineralPositions.isEmpty()) {               // Hay minerales cerca
                        asignValue = this.board[this.x_current][this.y_current];
                        move(nearMineralPositions, 0);
                        this.hasMineral = true;
                    } else if (!nearCrumbPositions.isEmpty()) {          // Seguir la migaja mas pequeña
                        PriorityQueue<int[]> nearCrumb = new PriorityQueue<>(Comparator.comparingInt(n -> n[2]));
                        for (int[] pos : nearCrumbPositions) {
                            nearCrumb.add(new int[]{pos[0], pos[1], this.board[pos[0]][pos[1]]});
                        }
                        nearCrumbPositions = new ArrayList<>();
                        nearCrumbPositions.add(nearCrumb.peek());

                        // Se mueve al mas pequeño
                        move(nearCrumbPositions, nearCrumb.peek()[2]);
                        asignValue = nearCrumb.peek()[2];
                    } else if (!nearEmptyPositions.isEmpty()) {          // No hay nada   
                        move(nearEmptyPositions, 0);
                        asignValue = 0;
                    }

                    graphic.setBoard(lastPosition, new int[]{this.x_current, this.y_current}, hasMineral, 0);
                    this.board = graphic.getBoardGame();
                }
            } catch (Exception e) {
                System.out.println("DIE");
                break;
            }
        }
    };

    private int[] getNearShip() {
        int minDistance = this.board.length + this.board[0].length;
        int minIndex = -1;
        int distance;
        for (int i = 0; i < this.shipsPosition.size(); i++) {
            distance = Math.abs(this.shipsPosition.get(i)[0] - this.x_current) + Math.abs(this.shipsPosition.get(i)[1] - this.y_current);
            if (distance < minDistance) {
                minDistance = distance;
                minIndex = i;
            }
        }
        return this.shipsPosition.get(minIndex);
    }

    private void move(ArrayList<int[]> moveListPositions, int replace) {
        int random = (int) (Math.random() * (moveListPositions.size()));

        int x_new = moveListPositions.get(random)[0];
        int y_new = moveListPositions.get(random)[1];

        this.board[x_new][y_new] = 2;
        this.board[this.x_current][this.y_current] = replace;

        this.x_current = x_new;
        this.y_current = y_new;
    }

    private ArrayList<int[]> getNearpositions(int x_position, int y_position) {
        ArrayList<int[]> nearPositions = new ArrayList<>();

        if (x_position - 1 >= 0) {
            nearPositions.add(new int[]{x_position - 1, y_position});
        }
        if (x_position + 1 < this.board.length) {
            nearPositions.add(new int[]{x_position + 1, y_position});
        }
        if (y_position - 1 >= 0) {
            nearPositions.add(new int[]{x_position, y_position - 1});
        }
        if (y_position + 1 < this.board[0].length) {
            nearPositions.add(new int[]{x_position, y_position + 1});
        }

        return nearPositions;
    }

    private int moveAvailable(int[] nearPosition) {
        if (nearPosition[0] >= 0 && nearPosition[0] < this.board.length && nearPosition[1] >= 0 && nearPosition[1] < this.board[0].length) {
            return this.board[nearPosition[0]][nearPosition[1]];
        } else {
            return -1;
        }
    }

    private ArrayList<int[]> pathFinder(int[] startPosition, int[] finishPosition) {
        ArrayList<int[]> movesList = new ArrayList<>();   // Lista de movimientos para llegar a la meta

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));   // Crea una cola de prioridad comparando f (f = g + h)
        Set<Node> closeSet = new HashSet<>();       // Solo queremos saber si ya existe o no

        Node root = new Node(startPosition[0], startPosition[1], calculateheristic(startPosition, finishPosition), null);
        openSet.add(root);

        while (!openSet.isEmpty()) {
            Node bestNode = openSet.poll();         // Obtenemos el menor de f
            if (Arrays.equals(bestNode.getPosition(), finishPosition)) {    // Si el mejor nodo es igual al nodo final reconstruimos el camino
                Node current = bestNode;
                while (current != null) {
                    movesList.add(current.getPosition());
                    current = current.father;
                }
                break;
            }

            closeSet.add(bestNode);

            ArrayList<int[]> nearPositions = getNearpositions(bestNode.x, bestNode.y);
            for (int[] pos : nearPositions) {
                if (this.board[pos[0]][pos[1]] == 4 || containsNode(closeSet, pos)) {
                    continue;
                }

                Node child = new Node(pos[0], pos[1], calculateheristic(pos, finishPosition), bestNode);
                if (!containsNode(openSet, child)) {
                    openSet.add(child);
                }
            }
        }
        return movesList;
    }

    public int calculateheristic(int[] start, int[] finish) {
        return Math.abs(finish[0] + start[0]) + Math.abs(finish[1] - start[1]);
    }

    private boolean containsNode(Collection<Node> nodes, int[] pos) {
        return nodes.stream().anyMatch(node -> node.x == pos[0] && node.y == pos[1]);
    }

    private boolean containsNode(Collection<Node> nodes, Node node) {
        return nodes.contains(node);
    }

    public void setPlayMode(boolean playMode) {
        this.playMode = playMode;
    }

    public void printBoard() {
        for (int j = 0; j < this.board.length; j++) {
            for (int i = 0; i < this.board[0].length; i++) {
                System.out.print("" + board[i][j] + ' ');
            }
            System.out.println("");
        }
        System.out.println("");
    }
}
