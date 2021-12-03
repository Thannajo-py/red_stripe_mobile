package com.example.filrouge

import java.util.jar.Manifest

val ALL_MECHANICS = ArrayList<NamedResultBean>()
val ALL_CATEGORIES = ArrayList<NamedResultBean>()
enum class SerialKey{
    Game,
    Type,
    Name,
    ToModifyDataId,
    ToModifyDataType,
    ToModifyDataName,
    APIUrl,
    APIStaticUrl,
    IsLocal,
    Timestamp,
    SavedUser,
    GameId,
    AddOnId,
    MultiAddOnId,
    GenericId,
    QueryContent,
    SaveDatabase,
    ApiBgaGame,
    ApiBgaImage,
    TempImage

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
    MultiAddOn

}

enum class MenuId{
    ApiSearch,
    Synchronize,
    Search,
    DeleteAccount,
    AddContent,
    DeleteThis,
    ModifyThis,
    CreateAccount,
    SynchronizeParameter,
    ChangePassword,
    LoadImages,
    ResetDB,
    Disconnect,
    SaveLocalDatabase,
    LoadLocalDatabase,
    TakePhoto,
    AddExternalLink,
    GetInternalFile,
    ResetImage,
    DeleteImage
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

enum class Constant(val value:String){
    ApiBgaKey("WgrVtRvHeo"),
    Extension("expansion"),
    UrlMechanics("https://api.boardgameatlas.com/api/game/mechanics?client_id=WgrVtRvHeo"),
    UrlCategories("https://api.boardgameatlas.com/api/game/categories?client_id=WgrVtRvHeo")
}

enum class PermissionRequest(val perm:String){
    Camera(android.Manifest.permission.CAMERA),
    ExternalStorage(android.Manifest.permission.READ_EXTERNAL_STORAGE)
}
