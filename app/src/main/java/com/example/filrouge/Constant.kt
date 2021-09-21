package com.example.filrouge

enum class APIUrl(val url:String){
    ALL_GAMES("https://fil-rouge-ja.herokuapp.com/api/synchronize-change/")
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
    RememberPasswordStorage,
    SearchResult,
    AccountName,
    AddedContent,
    APIDeleteStorage,
    APIModifyStorage,
    APIAddStorage,
    ToModifyData

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
    Topic,
    Search,
    Game,
    AddOn,
    MultiAddOn,
}

enum class MenuId{
    CancelAndSynchronize,
    Synchronize,
    Search,
    DeleteAccount,
    AddContent,
    DeleteThis,
    ModifyThis
}