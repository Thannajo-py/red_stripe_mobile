package com.example.filrouge

import java.util.regex.Pattern

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
    APIDeleteStorage,
    APIModifyStorage,
    APIAddStorage,
    ToModifyData,
    AllImagesStorage,
    AllUsersStorage,
    APIUrl,
    APIStaticUrl,
    APILastSave

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

}

enum class MenuId{
    CancelAndSynchronize,
    Synchronize,
    Search,
    DeleteAccount,
    AddContent,
    DeleteThis,
    ModifyThis,
    CreateAccount,
    SynchronizeParameter,
    ChangePassword
}

enum class RegexPattern(val pattern:String){
    PassWord("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{6,}$")
}

enum class CommonString(val string:String){
    PassWordRequirement("le login ne doit pas être vide\n" +
            "le mot de passe doit contenir au moins:\n" +
            "- 1 lettre minuscule\n" +
            "- 1 lettre majuscule\n" +
            "- 1 nombre\n" +
            "- 1 caractère spécial\n" +
            "- 6 caractères")
}

enum class AddedContent{
    Designer,
    Artist,
    Publisher,
    Tag,
    Topic,
    Language,
    Mechanism,
    PlayingMod
}

