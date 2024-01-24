package com.pemrogandroid.fomo

class Note
{
    private lateinit var title:String
    private lateinit var description:String
    private lateinit var imageUrl: String// Add this field for the image URL
    private var userName:String=""
    private var longitude:Double=0.0
    private var latitude:Double=0.0
    private var locationName:String=""
    private var date:String=""
    private var time:String=""
    private var genre:String=""
    private var uploadId:String=""
    private var dateAndTime:String=""




    constructor(title: String, description: String,imageUrl: String,userName:String,longitude:Double,latitude:Double,locationName:String,date:String,time:String,genre:String,uploadId:String) {
        this.title = title
        this.description = description
        this.imageUrl = imageUrl
        this.userName=userName
        this.longitude=longitude
        this.latitude=latitude
        this.locationName=locationName
        this.date=date
        this.time=time
        this.genre=genre
        this.uploadId=uploadId

    }

    constructor() {
        // Default constructor needed for firestore
    }

    fun getTitle():String
    {
        return title
    }
    fun getDescription():String
    {
        return description
    }
    fun getImageUrl():String
    {
        return imageUrl
    }
    fun setImageUrl(imageUrl: String)
    {
        this.imageUrl=imageUrl
    }
    fun getUserName():String
    {
        return  userName
    }
    fun setUserName(userName: String)
    {
        this.userName=userName
    }
    fun getLatitude():Double
    {
        return  latitude
    }
    fun getLongitude():Double
    {
        return  longitude
    }
    fun getLocationName():String
    {
        return  locationName
    }
     fun getDate():String{
        return date
    }
    fun getTime():String
    {
        return time
    }

    fun getDateAndTime(): String {
        return getDate()+" "+ getTime()
    }
    fun getGenre():String{
        return genre
    }
    fun getUploadId():String
    {
        return uploadId
    }

}