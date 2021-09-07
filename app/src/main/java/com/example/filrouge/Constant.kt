package com.example.filrouge

enum class APIUrl(val url:String){
    ALL_GAMES("https://fil-rouge-ja.herokuapp.com/api/all/")
}

enum class SerialKey{
    Game,
    AddOn,
    ParentGame,
    MultiAddOn,
    Type,
    Name,
    APIStorage,
    RememberNameStorage,
    RememberPasswordStorage
}

enum class Type{
    Designer,
    Artist,
    Publisher,
    PlayingMode,
    Difficulty,
    Language,
    Tag,
    Mechanism,
    Topic
}

enum class MenuId{
    Synchronize
}