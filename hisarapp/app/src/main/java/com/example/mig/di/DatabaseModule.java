package com.example.mig.di;

//@Module
//@InstallIn(ApplicationComponent.class)
//public class DatabaseModule {

//    @Provides
//    @Singleton
//    public static HisarDatabase provideHisarDB(Application application){
//        return Room.databaseBuilder(application, HisarDatabase.class, Constants.DataBaseName)
//                .fallbackToDestructiveMigration()
//                .allowMainThreadQueries()
//                .build();
//    }

//    @Provides
//    @Singleton
//    public static HisarDao providePokeDao(HisarDatabase hisarDB){
//        return hisarDB.hisarDao();
//    }
//}