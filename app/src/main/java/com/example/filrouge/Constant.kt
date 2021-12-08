package com.example.filrouge

import com.example.filrouge.bean.*


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

}


enum class Type{
    Designer,
    Artist,
    Publisher,
    PlayingMod,
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

enum class SaveDbField{
    game,
    addOn,
    multiAddOn,
    tag,
    topic,
    mechanism,
    difficulty,
    designer,
    artist,
    publisher,
    playingMod,
    language,
    gameMultiAddOn,
    gameTag,
    gameTopic,
    gameMechanism,
    gameDesigner,
    addOnDesigner,
    multiAddOnDesigner,
    gameArtist,
    addOnArtist,
    multiAddOnArtist,
    gamePublisher,
    addOnPublisher,
    multiAddOnPublisher,
    gamePlayingMod,
    addOnPlayingMod,
    multiAddOnPlayingMod,
    gameLanguage,
    addOnLanguage,
    multiAddOnLanguage,
    deletedContent,
    image,
    user,
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
    DeleteImage,
    DeleteObject,
}


enum class RegexPattern(val pattern:String){
    PassWord("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{6,}$")
}


enum class AddedContent{
    Designer,
    Artist,
    Publisher,
    Tag,
    Topic,
    Language,
    Mechanism,
    PlayingMod,
    Difficulty,
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
