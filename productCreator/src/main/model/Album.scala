package main.model

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

class Album(val albumid: String) {  
  val trackList = Map[String, String]() //map from songid to filename
  

 def addTrack(sfid: String, fileName:String) = {
    trackList.put(sfid, fileName)
  }
  
  // TO DO change this to functional iteration of map
 def getAlbumIds: String = {
   val albumIds = ListBuffer[String]()
   for ( (songid, fileName) <- trackList){ albumIds += "SF" + songid} 
   albumIds.mkString(" OR ")
 }
 
}
