package main.model

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map

/**
 * Album class represents an album with two fields.
 * 1 Album id String representing the album's unique identifier.
 * 2 A String -> String Map of song id to filename representing the songs on the album.
 */
class Album(val albumid: String) {  
  // Map from songid to filename
  val trackList = Map[String, String]() 
  
  def addTrack(sfid: String, fileName:String) = trackList.put(sfid, fileName)
  
  // TO DO change this to functional iteration of map
  def getAlbumIds: String = {
   val albumIds = ListBuffer[String]()
   for ( (songid, fileName) <- trackList){ albumIds += "SF" + songid} 
   albumIds.mkString(" OR ")
 }
  
}