package main.view

import main.controller.ProductCreator
import processing.core._
import java.io.File
import controlP5._

 
/**
 * @author julian
 */
// TO DO -  Improve this so not null start.  Have to initialise by setup() but also can't be left
// unitialised else class
// UI logic is very complex and bug prone, especially in relation to data validation.  Refactor
class ProdCreatorViewer extends PApplet{
  var controller: ProductCreator = null
  
  var cp5: ControlP5 = null
  var myTextarea: Textarea = null
 
  var assetDir, formatDir, outDir, dataDir: String = ""
  var assetDirSet, formatDirSet, outDirSet, dataDirSet, inputValid: Boolean = false
  var assetDirSetTemp,formatDirSetTemp, outDirSetTemp, dataDirSetTemp: Boolean = false
  
  //checkbox
  var checkbox: CheckBox = null
  var xml, mp4, mp3g, bin = false //correspond to 0,1,2,3 in 'controlEvent'
  //var running = false;
  
  //Text input
  var albums:Array[String] = null;
  var textfield: Textfield = null
  
  override def settings() {
  size(300,510) 
  }
  
  override def setup(){
    
    
    cp5 = new ControlP5(this);
    var font: PFont = createFont("arial",20);
    textFont(font);
    
    
    cp5.addButton("assets")
    .setBroadcast(false)  
    .setValue(100)
    .setPosition(40,40)
    .setSize(50,20)
    .setBroadcast(true);
    
     cp5.addButton("formats")
    .setBroadcast(false)  
    .setValue(100)
    .setPosition(110,40)
    .setSize(50,20)
    .setBroadcast(true);
    
    cp5.addButton("output")
    .setBroadcast(false)  
    .setValue(100)
    .setPosition(40,80)
    .setSize(50,20)
    .setBroadcast(true);
    
     cp5.addButton("data")
    .setBroadcast(false)  
    .setValue(100)
    .setPosition(110,80)
    .setSize(50,20)
    .setBroadcast(true);
    
    textfield = cp5.addTextfield("input")
                 .setPosition(40,120)
                 .setSize(200,40)
                 .setFont(font)
                 .setFocus(true)
                 .setColor(color(255,0,0));
    textfield.setAutoClear(false);
    
    checkbox = cp5.addCheckBox("checkBox")
              .setPosition(40, 190)
              .setSize(20, 20)
              .setItemsPerRow(2)
              .setSpacingColumn(60)
              .setSpacingRow(20)
              .addItem("xml", 0)
              .addItem("mp4", 50)
              .addItem("mp3g", 100)
              .addItem("bin", 150)
              ;
    
   cp5.addButton("go")
    .setBroadcast(false)  
    .setValue(100)
    .setPosition(40,270)
    .setSize(50,20)
    .setBroadcast(true); 
    
    
    myTextarea = cp5.addTextarea("txt")
      .setPosition(40,310)
      .setSize(200,180)
      .setFont(createFont("arial",12))
      .setLineHeight(14)
      .setColor(color(128))
      .setColorBackground(color(255,100))
      .setColorForeground(color(255,100));
    

  }
  
  override def draw(){
    
    background(0);
     fill(255);
  }
  
  /*----------------------------------------------------------
    SELECT FOLDERS
   ----------------------------------------------------------*/
    def assets {
      if( ! assetDirSet) {
        assetDirSetTemp = true
        selectFolder("Select an asset directory", "selected")
      } else{
        printText("Asset directory is already set");
      }
  }
 
    
   def formats {
    if( ! formatDirSet) {
        formatDirSetTemp = true
        selectFolder("Select a format directory", "selected")
      } else{
        printText("Format directory is already set");
      }
  }  
    
    
  def output {
    if( ! outDirSet) {
        outDirSetTemp = true
        selectFolder("Select an output directory", "selected")
      } else{
        printText("Output directory is already set");
      }
  }
  
   def data {
    if( ! dataDirSet) {
        dataDirSetTemp = true
        selectInput("Select a data file", "fileSelected");
      } else{
        printText("Data file is already set");
      }
  }
  
   
   
  def selected(selection: File) {
      if (selection == null) {
        printText("Window was closed or the user hit cancel.");
        assetDirSetTemp = false
        outDirSetTemp = false
        formatDirSetTemp = false
        dataDirSetTemp = false
      } else if(assetDirSetTemp){
        myTextarea.clear()
        printText("Asset directory is " + selection.getAbsolutePath());
        assetDir =  selection.getAbsolutePath()
        assetDirSet = true;
        assetDirSetTemp = false;
      } else if(formatDirSetTemp){
        myTextarea.clear()
        printText("Format directory is " + selection.getAbsolutePath());
        formatDir =  selection.getAbsolutePath()
        formatDirSet = true;
        formatDirSetTemp = false;
      } else if(outDirSetTemp) {
         myTextarea.clear()
        printText("Output directory is " + selection.getAbsolutePath());
        outDir =  selection.getAbsolutePath()
        outDirSet = true;
        outDirSetTemp = false;
      } 
   } 
  
  def controlEvent(theEvent: ControlEvent) {
    if (theEvent.isFrom(checkbox)) {
      if(checkbox.getArrayValue(0) == 1.0) xml =  true else xml =  false
      if(checkbox.getArrayValue(1) == 1.0) mp4 =  true else mp4 =  false
      if(checkbox.getArrayValue(2) == 1.0) mp3g = true else mp3g = false
      if(checkbox.getArrayValue(3) == 1.0) bin =  true else bin =  false
    }
  }
  
  def fileSelected(selection: File){
    if (selection == null) {
      printText("Window was closed or the user hit cancel.")
      } else {
        myTextarea.clear()
        printText("Data file is " + selection.getAbsolutePath());
        dataDir =  selection.getAbsolutePath()
        dataDirSet = true;
        dataDirSetTemp = false;
      }
    
  }
 
  
  
  
/*----------------------------------------------------------
    GET ALBUMS
 ----------------------------------------------------------*/
//START HERE
/*
   * Automatically receives results from controller input
   */
  
  def input(theText: String){
    if(controller == null){
     printText("Error: Please set all directories / formats and hit GO first");
    } else {
    /* if any items can't be found in data then return false and throw error message*/
    theText.split(",").foreach { 
      album =>
      controller.getAlbum(album) match{
        case None =>  printText("Error: " + album + " does not exist in data")
                       inputValid = false
                       return
        case Some(x) => //do nothing
      }  
    } 
    inputValid = true
     myTextarea.clear()
    printText("Input valid")
    albums = theText.split(",")  
    
    }
  }

  
  
  /*----------------------------------------------------------
    GO - get files
   ----------------------------------------------------------*/
  //TO DO - Refactor / rationalise
  def go{
    
    if(controller == null) {
      initialiseProductCreator
      return
    }
    
    if(! formatSelect){
       myTextarea.clear()
      printText("Error: Please select at least one format")
       return
    }
    
     
     if(!inputValid){
        myTextarea.clear() 
       printText("Error: Please input valid data");
         return
       } else {
          albums.foreach { album =>  
            if(xml)controller.createProduct(album, "xml") 
            
            if(mp4) controller.createProduct(album, "mp4HD")
            
            if(mp3g) controller.createProduct(album, "mp3g320")
            
            if(bin) controller.createProduct(album, "bin")
            
            printText("finished album: " + album + "\n") 
          }
          albums = null
          inputValid = false
          textfield.clear()
          printText("Ready to process more albums")
          
     }  
      
    //helper functions
    def formatSelect: Boolean = return xml || mp4 || mp3g || bin
    def productCreatorInitialised: Boolean = return return assetDirSet & formatDirSet & outDirSet & dataDirSet
  }

  
  
  def initialiseProductCreator = {
    if(this.controller == null && prodCreateReadyForInit){
      controller = new ProductCreator(assetDir, formatDir, outDir, dataDir, this)
       myTextarea.clear()
      printText("Product creator initialised.  Please enter albums, press enter for validation and hit go again")
    } else {
     printText("Error: Please set all directories before running and at least one format."); 
    } 
    def prodCreateReadyForInit: Boolean = return return assetDirSet & formatDirSet & outDirSet & dataDirSet
  } 
  
  
  def printText(text:String) = {
    myTextarea.setText(myTextarea.getText + "\n" + text);
  }
      
  
}