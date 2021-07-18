package ru.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ShortestPathSearcher {
    private static final int GRID_SIZE = 1000;

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            double s = Double.parseDouble(br.readLine());
            int n = Integer.parseInt(br.readLine());
            Coord[] coords = new Coord[n];
            for (int i = 0; i < n; i++) {
                String coord = br.readLine();
                String[] splitCoord = coord.split(" ");
                coords[i] = new Coord(Double.parseDouble(splitCoord[0]), Double.parseDouble(splitCoord[1]));
            }

            ShortestPathSearcher searcher = new ShortestPathSearcher();
            double ev = findShortestPathLength(coords, s);
            System.out.println(ev);
            System.out.println(calcProbability(ev));
        }
    }

    public static double findShortestPathLength(Coord[] coords, double s) {
        double[][] heights = heights(coords, s);
        PriorityQueue<Cell> q = new PriorityQueue<>();
        Cell[][] cells = new Cell[GRID_SIZE + 1][GRID_SIZE + 1];
        Cell start = new Cell((GRID_SIZE + 1) / 2, 0, heights[(GRID_SIZE + 1) / 2][0]);
        Cell stop = new Cell((GRID_SIZE  + 1) / 2, GRID_SIZE, 0);
        double cellSize = s / GRID_SIZE / 2;
        double diagSize = s / GRID_SIZE * Math.sqrt(2) / 2;

        q.add(start);
        cells[(GRID_SIZE + 1) / 2][0] = start;
        while(!q.isEmpty()) {
//            System.out.println(q.size());
            Cell cell = q.poll();
            if(cell.isVisited) {
                continue;
            }

//            System.out.println(cell);

            cell.isVisited = true;
            if(cell.equals(stop)) {
                return cell.d;
            }

            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    int newI = i + cell.x;
                    int newJ = j + cell.y;
                    if (0 <= newI && newI <= GRID_SIZE &&
                            0 <= newJ && newJ <= GRID_SIZE) {
                        double delta;
                        if (i != 0 && j != 0) {
                            delta = diagSize;
                        } else if(i != 0 || j != 0){
                            delta = cellSize;
                        } else {
                            continue;
                        }

                        double newD = cell.d + delta * (heights[cell.x][cell.y] + heights[newI][newJ]);
                        Cell newCell = cells[newI][newJ];
                        //todo add priority queue with decrease element
                        if(newCell == null) {
                            cells[newI][newJ] = new Cell(newI, newJ, newD);
                            q.add(cells[newI][newJ]);
                        } else if(newCell.d > newD) {
                            newCell.d = newD;
                            q.add(newCell);
                        }
                    }
                }
            }
        }

        return -1;
    }

    public static double[][] heights(Coord[] coords, double s) {
        double[][] heights = new double[GRID_SIZE + 1][GRID_SIZE + 1];
        double cellSize = s / GRID_SIZE;
        double sSquare = s * s;
        double PiSquare = Math.PI * Math.PI;
        for (int i = 0; i < heights.length; i++) {
            for (int j = 0; j < heights.length; j++) {
                for (Coord coord : coords) {
                    double iCoord = cellSize * i;
                    double jCoord = cellSize * j;
                    double v = -(PiSquare *
                            ((iCoord - coord.x) * (iCoord - coord.x) + (jCoord - coord.y) * (jCoord - coord.y))) / sSquare;
                    heights[i][j] += Math.pow(Math.E, v);
//                    System.out.println("pow " + heights[i][j] + " " + iCoord + " " + jCoord);
                }

//                System.out.println(heights[i][j]);
            }
        }

        return heights;
    }

    public static double calcProbability(double expectedValue) {
        return Math.pow(Math.E, -expectedValue);
    }

    private static class Cell implements Comparable<Cell> {
        int x;
        int y;
        double d;
        boolean isVisited;

        Cell(int x, int y, double d) {
            this.x = x;
            this.y = y;
            this.d = d;
            isVisited = false;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return x == cell.x && y == cell.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public int compareTo(Cell o) {
            return Double.compare(d, o.d);
        }

        @Override
        public String toString() {
            return "Cell{" +
                    "x=" + x +
                    ", y=" + y +
                    ", d=" + d +
                    ", isVisited=" + isVisited +
                    '}';
        }
    }

    record Coord(double x, double y){}
}
