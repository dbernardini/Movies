package com.bernardini.danilo.movies.database;

public final class DBContract {

    public static final String SEEN_MOVIES_TABLE_NAME = "seen_movies";
    public static final String OWN_MOVIES_TABLE_NAME = "own_movies";
    public static final String WISH_MOVIES_TABLE_NAME = "wish_movies";
    public static final String SEEN_TV_TABLE_NAME = "seen_tv";
    public static final String WATCHING_TV_TABLE_NAME = "watching_tv";
    public static final String WISH_TV_TABLE_NAME = "wish_tv";

    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String PATH = "path";
    public static final String NEW = "new";
    public static final String DELETE = "remove";

    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";

    public static final String CREATE_TABLE_SEEN_MOVIES =
            "CREATE TABLE " + SEEN_MOVIES_TABLE_NAME + " (" +
                    ID + TEXT_TYPE + " PRIMARY KEY," +
                    TITLE + TEXT_TYPE + COMMA_SEP +
                    PATH + TEXT_TYPE +
                    " )";

    public static final String CREATE_TABLE_OWN_MOVIES =
            "CREATE TABLE " + OWN_MOVIES_TABLE_NAME + " (" +
                    ID + TEXT_TYPE + " PRIMARY KEY," +
                    TITLE + TEXT_TYPE + COMMA_SEP +
                    PATH + TEXT_TYPE +
                    " )";

    public static final String CREATE_TABLE_WISH_MOVIES =
            "CREATE TABLE " + WISH_MOVIES_TABLE_NAME + " (" +
                    ID + TEXT_TYPE + " PRIMARY KEY," +
                    TITLE + TEXT_TYPE + COMMA_SEP +
                    PATH + TEXT_TYPE +
                    " )";

    public static final String CREATE_TABLE_SEEN_TV =
            "CREATE TABLE " + SEEN_TV_TABLE_NAME + " (" +
                    ID + TEXT_TYPE + " PRIMARY KEY," +
                    TITLE + TEXT_TYPE + COMMA_SEP +
                    PATH + TEXT_TYPE +
                    " )";

    public static final String CREATE_TABLE_WATCHING_TV =
            "CREATE TABLE " + WATCHING_TV_TABLE_NAME + " (" +
                    ID + TEXT_TYPE + " PRIMARY KEY," +
                    TITLE + TEXT_TYPE + COMMA_SEP +
                    PATH + TEXT_TYPE +
                    " )";

    public static final String CREATE_TABLE_WISH_TV =
            "CREATE TABLE " + WISH_TV_TABLE_NAME + " (" +
                    ID + TEXT_TYPE + " PRIMARY KEY," +
                    TITLE + TEXT_TYPE + COMMA_SEP +
                    PATH + TEXT_TYPE +
                    " )";

    public static final String DELETE_TABLE_SEEN_MOVIES =
            "DROP TABLE IF EXISTS " + SEEN_MOVIES_TABLE_NAME;

    public static final String DELETE_TABLE_OWN_MOVIES =
            "DROP TABLE IF EXISTS " + OWN_MOVIES_TABLE_NAME;

    public static final String DELETE_TABLE_WISH_MOVIES =
            "DROP TABLE IF EXISTS " + WISH_MOVIES_TABLE_NAME;

    public static final String DELETE_TABLE_SEEN_TV =
            "DROP TABLE IF EXISTS " + SEEN_TV_TABLE_NAME;

    public static final String DELETE_TABLE_WATCHING_TV =
            "DROP TABLE IF EXISTS " + WATCHING_TV_TABLE_NAME;

    public static final String DELETE_TABLE_WISH_TV =
            "DROP TABLE IF EXISTS " + WISH_TV_TABLE_NAME;


    // To prevent someone from accidentally instantiating the contract class, give it an empty constructor.
    public DBContract() {
    }

}