package com.example.filrouge.utils

import com.example.filrouge.NamedResultBean

/**
 * A temporary list containing all Board Game Atlas mechanics, reset when application closed
 */
val ALL_MECHANICS by lazy { ArrayList<NamedResultBean>() }
/**
 * A temporary list containing all Board Game Atlas categories, reset when application closed
 */
val ALL_CATEGORIES by lazy { ArrayList<NamedResultBean>() }


/**
 * An enum of serialKey used with shared preferences to recover variable,
 * or to pass variable between activities
 */
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


/**
 * List of different game related type use to identify string
 * and with reflection to get proper members
 */
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


/**
 * Used with reflection to save all field from database
 */
enum class SaveDbField{
    Game,
    AddOn,
    MultiAddOn,
    Tag,
    Topic,
    Mechanism,
    Difficulty,
    Designer,
    Artist,
    Publisher,
    PlayingMod,
    Language,
    GameMultiAddOn,
    GameTag,
    GameTopic,
    GameMechanism,
    GameDesigner,
    AddOnDesigner,
    MultiAddOnDesigner,
    GameArtist,
    AddOnArtist,
    MultiAddOnArtist,
    GamePublisher,
    AddOnPublisher,
    MultiAddOnPublisher,
    GamePlayingMod,
    AddOnPlayingMod,
    MultiAddOnPlayingMod,
    GameLanguage,
    AddOnLanguage,
    MultiAddOnLanguage,
    DeletedContent,
    Image,
    User,
}


/**
 * Used to identify menu item selected
 */
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


/**
 * Enforce password rules regex
 */
enum class RegexPattern(val pattern:String){
    PassWord("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$")
}


/**
 * A set of other constant grouped to avoid magical String
 */
enum class Constant(val value:String){
    ApiBgaKey("WgrVtRvHeo"),
    Extension("expansion"),
    UrlMechanics("https://api.boardgameatlas.com/api/game/mechanics?client_id=WgrVtRvHeo"),
    UrlCategories("https://api.boardgameatlas.com/api/game/categories?client_id=WgrVtRvHeo")
}


/**
 * All necessary on the fly permissions
 */
enum class PermissionRequest(val perm:String){
    Camera(android.Manifest.permission.CAMERA),
    ExternalStorage(android.Manifest.permission.READ_EXTERNAL_STORAGE)
}
