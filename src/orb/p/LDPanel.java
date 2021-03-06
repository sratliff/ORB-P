package orb.p;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.JOptionPane;

/**
 * @author MWatkins
 */
public class LDPanel extends LevelPanel {

    ArrayList<String> output;
    String outputTxt = "";
    int tileLibOffset = 1;
    ArrayList<Tile> tileLibrary;
    Tile godsTile;
    hudObject tilePreview;
    int tilePrevLoc;
    Tile holeTile;

    LDPanel() {
        super();
        output = new ArrayList();
        loadTileLib();
        //now load tile library
        //countTiles();
    }

    @Override
    protected void handleClickedTile(Tile clicked) {
        if (clicked.isWithin(myClick.getX() - xOffset, myClick.getY() - yOffset)) {//if we aren't dragging
            clicked.setHighlight(!clicked.getHighlight());//toggle its selected status
        }
        if (clicked.getHighlight()) {//if it is now highlighted
            System.out.println("Tile ID: " + clicked.getID() + " " + clicked.getLoc() + " " + clicked.getGraphPath());
            //print tile info
        }
    }

    @Override
    protected void buildHUD() {

        super.buildHUD();
        buildTileCommander();

    }

    @Override
    protected void hudAction(hudObject hudOb) {
        System.out.println(hudOb.getAction());
        super.hudAction(hudOb);
        if (hudOb.matches("save")) {
            System.out.println("SAVING");
            saveLevel();
        }
        if (hudOb.matches("clear")) {
            System.out.println("CLEARING SELECTION");
            clearSelection();
        }
        if (hudOb.matches("delete")) {
            System.out.println("DELETING SELECTED");
            deleteSelection();
        }
        if (hudOb.matches("fill")) {
            System.out.println("FILLING SELECTED");
            fillSelection();
        }
        if (hudOb.contains("row")) {
            int selectedTile = Integer.parseInt(hudOb.getAction().substring(3));
            System.out.println("CLICKEDONROW:" + hudOb.getAction());
            Tile toReplace = new Tile(godsTile.getXMin(), godsTile.getYMin());
            String selectedTileS = String.format("%03d", selectedTile);
            toReplace.setGraphic("pics/tiles/tile" + selectedTileS + ".png");
            System.out.println("setting graphic: pics/tiles/tile" + selectedTileS + ".png");
            toReplace.setID(selectedTile);
            setGodsTile(toReplace);
        }
    }

    protected void saveLevel() {
        fillOutput();
        saveOutput();
    }

    protected void fillOutput() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        outputTxt = timeStamp + ".lvl";

        title = JOptionPane.showInputDialog("Level Name: ");
        outputTxt = title + ".lvl";
        output.add("<def>");
        output.add("<bgidef>pics/backgrounds/gamePanel.png</bgidef>");

        ArrayList<String> defPaths = new ArrayList();
        defPaths.add("pics/tiles/tile000.png");

        //adding defpaths
        for (int i = 0; i < tiles.size(); i++) {
            String tilePath = tiles.get(i).getGraphPath();
            boolean found = false;
            for (int j = 0; j < defPaths.size(); j++) {
                if (tilePath.compareToIgnoreCase(defPaths.get(j)) == 0) {
                    found = true;
                }
            }
            if (!found) {
                defPaths.add(tilePath);
            }
        }

        //making tiledefs
        for (int i = 0; i < defPaths.size(); i++) {
            output.add("<tiledef>");
            output.add("<id>" + i + "</id>");
            if (i == 0) {
                output.add("<cost>0</cost>");
            } else {
                output.add("<cost>1</cost>");
            }
            output.add("<path>" + defPaths.get(i) + "</path>");
            output.add("</tiledef>");
        }

        output.add("</def>");
        output.add("<board>");
        output.add("<title>" + title + "</title>");
        output.add("<width>50</width>");
        output.add("<height>50</height>");
        output.add("<default>0</default>");
        output.add("<fill>true</fill>");

        for (int i = 1; i < defPaths.size(); i++) {
            output.add("<overwrite>");
            output.add("<type>" + i + "</type>");
            for (int j = 0; j < tiles.size(); j++) {
                if (tiles.get(j).getGraphPath().compareToIgnoreCase(defPaths.get(i)) == 0) {
                    String tileAdd = tiles.get(j).getLoc();
                    tileAdd = "<tile>" + tileAdd + "</tile>";
                    output.add(tileAdd);
                } else {
                    //for debugging:
                    // System.out.println(tiles.get(j).getGraphPath() + " " + ORBP.libraryPath+defPaths.get(i));
                }
            }
            output.add("</overwrite>");
        }

        output.add("</board>");
    }

    protected void saveOutput() {
        try {
            PrintWriter writer = new PrintWriter(ORBP.libraryPath + "levels/" + outputTxt, "UTF-8");//for output file

            for (int i = 0; i < output.size(); i++) {
                writer.println(output.get(i));
            }
            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        output = new ArrayList();
    }

    private void clearSelection() {
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).setHighlight(false);
        }
    }

    private void loadTileLib() {
        tileLibrary = new ArrayList();//to store the different TYPES of tiles
        //folder where tiles are stored
        File tileFolder = new File(ORBP.libraryPath + "pics/tiles");
        File[] tilePics = tileFolder.listFiles();
        holeTile = new Tile(0, 0);
        holeTile.setGraphic("pics/tiles/tile000.png");
        for (int i = 0; i < tilePics.length; i++) {
            String path = tilePics[i].getName();
            //System.out.println(path);
            Tile libTile = new Tile(0, 0);
            libTile.setGraphic("pics/tiles/" + path);
            libTile.setID(i);
            tileLibrary.add(libTile);
        }
        System.out.println("tilelib size:" + tileLibrary.size());
        for (int i = 0; i < tileLibrary.size(); i++) {
            System.out.println(tileLibrary.get(i).graphPath);
        }

        godsTile = tileLibrary.get(1);
        System.out.println("god tile:" + godsTile.getGraphPath());
        tilePreview = new hudObject(130, 345, 160, 100, godsTile.getGraphPath(), "");
        hudObjects.add(tilePreview);
        tilePrevLoc = hudObjects.size() - 1;
    }

    protected void checkKey() {
        super.checkKey();
        if (myPress.getKeyPressed("delete")) {
            deleteSelection();
        }
        if (myPress.getKeyPressed("fill")) {
            fillSelection();
        }
        if (myPress.getKeyPressed("clear")) {
            clearSelection();
        }
    }

    private void deleteSelection() {
        for (int i = 0; i < tiles.size(); i++) {
            if (tiles.get(i).getHighlight()) {
                tiles.get(i).replaceWith(holeTile);
            }
        }
    }

    private void fillSelection() {
        System.out.println("filling with: " + godsTile.getGraphPath());
        for (int i = 0; i < tiles.size(); i++) {
            if (tiles.get(i).getHighlight()) {
                tiles.get(i).replaceWith(godsTile);
            }
        }
    }

    private void setGodsTile(Tile replaceTo) {
        godsTile.replaceWith(replaceTo);
        tilePreview = new hudObject(130, 345, 160, 100, godsTile.getGraphPath(), "");
        hudObjects.set(tilePrevLoc, tilePreview);
    }

    private void buildTileCommander() {
        hudObject tilesTab = new hudObject(0, 0, 388, 316, "pics/hud/leveldesigner/tilesTab.png", "");
        int tileRows = 18;//how many rows can be displayed at a time
        int ypos = 22;//starting y position of first row

        //hardcoded sizes: I got these from looking at the actual images used.
        hudObject buttonBg = new hudObject(4, 316, 381, 157, "pics/hud/leveldesigner/buttonBg.png", "");
        hudObject saveMap = new hudObject(buttonBg.getXMin(), 316, 100, 40, "pics/hud/leveldesigner/saveButton.png", "save");
        hudObject newMap = new hudObject(buttonBg.getXMin(), 356, 100, 40, "pics/hud/leveldesigner/newButton.png", "new");
        hudObject deleteSelection = new hudObject(buttonBg.getXMin() + 281, 316, 100, 40, "pics/hud/leveldesigner/deleteButton.png", "delete");
        hudObject fillSelection = new hudObject(buttonBg.getXMin() + 281, 356, 100, 40, "pics/hud/leveldesigner/fillButton.png", "fill");
        hudObject clearSelection = new hudObject(buttonBg.getXMin() + 281, 396, 100, 40, "pics/hud/leveldesigner/clearButton.png", "clear");
       // hudObject clearSelection = new hudObject(buttonBg.getXMin() + 281, 396, 100, 40, "pics/hud/charSetup/bar.png", "clear");

        //this section calculates how many different tiles there are:
//this for loop adds a row for every tile inside the tiles folder
        //in the picture's library. it should auto-detect each new addition.
        for (int rows = 0; rows < tileRows; rows++) {
            //again, these constants are delicate numbers based off of the actual files
            hudObject newRow = new hudObject(3, ypos, 381, 17, "pics/hud/leveldesigner/row0" + ((rows % 2) + 1) + ".png", "row" + rows);
            hudString rowTileText = new hudString("Tile " + (rows + tileLibOffset), 7, ypos - 2);
            // rowTileText = 
            ypos += 16;
            hudObjects.add(newRow);
            hudFonts.add(rowTileText);
        }
        Tile temp = new Tile(0, 0);
        temp.setGraphic("pics/tiles/tile001.png");
        godsTile = temp;
        holeTile = temp;
        hudObjects.add(buttonBg);
        hudObjects.add(saveMap);
        hudObjects.add(newMap);
        hudObjects.add(clearSelection);
        hudObjects.add(fillSelection);
        hudObjects.add(deleteSelection);
        hudObjects.add(tilesTab);
    }

    private void countTiles() {
        File folder = new File(ORBP.libraryPath + "pics/tiles");
        File[] listOfFiles = folder.listFiles();
        int numTileTypes = listOfFiles.length;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                System.out.println("File " + listOfFiles[i].getName());
            }
        }
    }

}
