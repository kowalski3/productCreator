package main.controller

import scala.collection.mutable.Map
import scala.io.Source._
import scala.collection.mutable.ListBuffer
import java.io._
import java.nio.file._
import main.model._
import main.view._
import findCopyFiles._ /*from scalaTools project */

/**
 * Controller class for ProductCreator
 * @author julian
 */
class ProductCreator( val assetSourceDirName: String, 
                      val formatSourceDirName: String,
                      val destDirName: String,
                      val view:ProdCreatorViewer) {
    
    val albumMap = Map[String,Album]()
  
    
  /**
   * Constructor
   */
  def this(assetSourceDirName: String, 
           formatSourceDirName: String, 
           destDirName: String, 
           dataFileName: String,
           view:ProdCreatorViewer) = {
    //TO DO, check for special characters, they're causing crash at the moment e.g Catch these errors / handle.
    this(assetSourceDirName, formatSourceDirName, destDirName, view)
       
    //TO DO - catch Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: 0
    for (lines <- fromFile(dataFileName).getLines()) {
       val line = lines.split(",")
       val albumid:String = line(0)
       val sfid:String = line(1) //need better of handling this
       val fileName:String = line(2)
     
       val result = albumMap.get(albumid)
       result match{
         case None => albumMap.put(albumid, new Album(albumid))        
         case Some(x) =>
       }
       albumMap.get(albumid).get.addTrack(sfid, fileName)
    }  
  }

 def getAlbum(albumName:String):Option[Album] = return albumMap.get(albumName) 
      
  /**
   * Takes albumid and format String arguments and finds, copies and renames files to destination directory 
   */
    def createProduct(albumid: String, format: String) = {
      //TO DO create format subdir if not already present
      val albumFolderPath = destDirName + "/" + format +  "/" + albumid
      //the path for album including sub dir
      val dir = new File(albumFolderPath);
      dir.mkdir();
      
      val suffixes = getFormat(format)
      val result = albumMap.get(albumid)
      val log = new PrintWriter(new File(destDirName + "/"+"errors.txt" ))
      
      
      
      result match{
         case None =>  view.printText("Product code " + albumid + " doesn't exist.")      
         case Some(album) =>
           for ((songid,trackName) <- result.get.trackList)
               try{
                 copyFiles(albumFolderPath, songid, suffixes, trackName, format) 
               } catch{
               case e: FileNotFoundException => log.write(e.toString()+"\n")  
               }     
       }
       log.close
     }
    
 /**
  * Gets the required file suffixes from the format argument
  */
 def getFormat(format:String):ListBuffer[String] = {
   val suffixes = new ListBuffer[String]
   //TO DO change this to enums / case objects
   format match {
     case "xml" => suffixes += ".xml"  += ".mp3"     //new source asset
     case "bin" => suffixes += ".bin"                //new source asset
     case "mp4HD" => suffixes += ".mp4"              //new source format
     case "mp3g320" => suffixes += ".cdg" += ".mp3"  //new source format  
     case _ => throw new IllegalArgumentException(format + " not valid. Valid options are xml / bin / mp4HD / mp3g320 / m4v / mov / mp3g128 ")
      }
    }
    
    
    /**
     * Copy files
     * Takes a song id argument, the album folder path, the file types, the new track name and the format.
     * Finds the file, copies it to the destination directory and renames it. 
     */
    //TO DO REFACTOR, TOO MANY PARAMETERS
    def copyFiles(albumFolderPath:String, songid:String, suffixes:ListBuffer[String], newTrackName:String, format:String) {   
      suffixes.foreach { suffix =>  
            val pattern = "SF" + songid + "*" + suffix
         
            val souceDirName = format match {
              case "xml" => assetSourceDirName
              case "bin" => assetSourceDirName
              case "mp4HD" => formatSourceDirName
              case "mp3g320" => formatSourceDirName
             }
            
            val filePath = FindFile.go(souceDirName, pattern)
//   view.printText(filePath)
             if (filePath != null) MyFileUtils.copyFile(new File(filePath.toString()), new File(albumFolderPath + "/" + newTrackName + suffix))    
       }
      
     }              
}