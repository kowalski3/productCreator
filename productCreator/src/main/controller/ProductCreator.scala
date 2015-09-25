package main.controller

/**
 * @author julian
 */


import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer
import java.io._
import java.nio.file._
import main.model._
import myUtils._ /*from scalaTools project */


/*
 * FORMATS 
 *  TO DO - Fix -> mixing java and scala io here.
 *  TO DO - Check data for special characters like , / ?  that might cause java.io.FileNotFoundException
 *  TO DO - To run quickly needs to map harddrive as it goes
  * TO DO - Deal better with files that aren't found
 */



class ProductCreator(
                      val assetSourceDirName: String, 
                      val formatSourceDirName: String,
                      val destDirName: String) {
    
    val albumMap = Map[String,Album]()
  
    
  /**
   * Constructor
   */
  def this(assetSourceDirName: String, 
           formatSourceDirName: String, 
           destDirName: String, 
           dataFileName: String) = {
    //TO DO, check for special characters, they're causing crash at the moment e.g Catch these errors / handle.
    this(assetSourceDirName, formatSourceDirName, destDirName)
    val dataFile = scala.io.Source.fromFile(dataFileName)
    
    //TO DO - catch Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: 0
    for (lines <- dataFile.getLines()) {
       // Get values from line
       val line = lines.split(",") 
       val albumid:String = line(0)
       val sfid:String = line(1) //need better of handling this
       val fileName:String = line(2)
     
       // Check if album already in map and add if not
       val result = albumMap.get(albumid)
       result match{
         case None => albumMap.put(albumid, new Album(albumid))        
         case Some(x) =>
       }
       albumMap.get(albumid).get.addTrack(sfid, fileName)
    } 
    
  }//constructor end
  
  
  /**
   *   Returns a list of file prefixes 
   */
  def getFileNames(albumid:String):String = {
    val result = albumMap.get(albumid)
       result match{
         case None =>    throw new RuntimeException("Error " + albumid + " cannot be found")
         case Some(x) => result.get.getAlbumIds.toString()  
       }
    
  }
  
  /**
   * Create Product
   */
    def createProduct(albumid: String, format: String) = {
      import java.io.File
      //TO DO create format subdir if not already present
      val albumFolderPath = destDirName + "/" + format +  "/" + albumid
      //the path for album including sub dir
      val dir = new File(albumFolderPath);
      dir.mkdir();
      
      val suffixes = getFormat(format)
      
      val result = albumMap.get(albumid)
      result match{
         case None => println("Product code " + albumid + " doesn't exist.")
         case Some(album) => 
           for ((songid,trackName) <- result.get.trackList)
               try{
                 println(songid)
//                 copyFiles(albumFolderPath, songid, suffixes, trackName, format) 
               } catch{
                 case e: RuntimeException => println(e)
               }     
       }
      
     }
    
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
     * Traverses the track in the album and calls copyFile for eachTrack
     * 
     */
    //TO DO REFACTOR, TOO MANY PARAMETERS
    def copyFiles(albumFolderPath:String, songid:String, suffixes:ListBuffer[String], newTrackName:String, format:String) {   
     
    
       val files = suffixes.foreach { suffix =>  
       val pattern = "SF" + songid + "*" + suffix
       //TO DO REFACTOR
      val souceDirName = format match {
        case "xml" => assetSourceDirName
        case "bin" => assetSourceDirName
        case "mp4HD" => formatSourceDirName
        case "mp3g320" => formatSourceDirName
 
       }
  
       
       
       
       val filePath = FindFile.go(souceDirName, pattern)
       if (filePath != null){
         MyFileUtils.copyFile(new File(filePath.toString()), new File(albumFolderPath + "/" + newTrackName + suffix))  
       }   
     }
     }     
      
      
}

 
//
//
///**
// * Runner object
// * 
// */
//object ProductCreatorRun extends App {
//      //TO DO change sourceDirName based on format
//      
//        //TO DO NEED REFATORING
//
//  
//  
//      val assetSourceDirName = "W:/SUNFLYGroundZERO/1 Assets"
//      val formatSourceDirName = "W:/SUNFLYGroundZERO/2 Video Formats"
//      val oldSource = "Z:/Sunfly Old Formats"  
//  
//      val destDirName = "C:/Julian/output/productCreator"
//      
//      
//      val dataFileName = "C:/Julian/git/scalaTools/data/ProductCreatorData.csv"
//      val pcreator = new ProductCreator(assetSourceDirName, formatSourceDirName, oldSource, destDirName, dataFileName)
//
//      
//     // val albumIds = for(i <- 331 to 336) yield {"SF" + i }
//      
//      val albumIds = Array("MW855")
//      //val formats = Array("bin")
//      //val formats = Array("xml","bin","mp4HD","mp3g320","m4v","mov","mp3g128")
// 
//      val formats = Array("bin")
//      albumIds.foreach { 
//        albumId => formats.foreach { 
//          format => pcreator.createProduct(albumId, format) 
//          }
//        }
//    
//}