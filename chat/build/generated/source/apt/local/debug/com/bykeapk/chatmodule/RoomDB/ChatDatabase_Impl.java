package com.bykeapk.chatmodule.RoomDB;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import com.bykeapk.chatmodule.RoomDB.DataAccessObject.ConversationDao;
import com.bykeapk.chatmodule.RoomDB.DataAccessObject.ConversationDao_Impl;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unchecked")
public class ChatDatabase_Impl extends ChatDatabase {
  private volatile ConversationDao _conversationDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `ChatConversationEntity` (`trip_id` TEXT NOT NULL, `conversationID` TEXT, `recieverID` TEXT, `createdTime` TEXT, `updatedTime` TEXT, PRIMARY KEY(`trip_id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `ChatMessageEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `_id` TEXT, `uuid` TEXT, `message` TEXT, `messageType` TEXT, `isRecieved` INTEGER NOT NULL, `localFilePath` TEXT, `serverFilePath` TEXT, `fileDuration` TEXT, `SenderID` TEXT, `tripID` TEXT, `createdMessageTime` TEXT, FOREIGN KEY(`tripID`) REFERENCES `ChatConversationEntity`(`trip_id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        _db.execSQL("CREATE  INDEX `index_ChatMessageEntity_tripID` ON `ChatMessageEntity` (`tripID`)");
        _db.execSQL("CREATE UNIQUE INDEX `index_ChatMessageEntity__id_uuid` ON `ChatMessageEntity` (`_id`, `uuid`)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"6a3ed5b5bb76e9a6a28730395c63708a\")");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `ChatConversationEntity`");
        _db.execSQL("DROP TABLE IF EXISTS `ChatMessageEntity`");
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        _db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsChatConversationEntity = new HashMap<String, TableInfo.Column>(5);
        _columnsChatConversationEntity.put("trip_id", new TableInfo.Column("trip_id", "TEXT", true, 1));
        _columnsChatConversationEntity.put("conversationID", new TableInfo.Column("conversationID", "TEXT", false, 0));
        _columnsChatConversationEntity.put("recieverID", new TableInfo.Column("recieverID", "TEXT", false, 0));
        _columnsChatConversationEntity.put("createdTime", new TableInfo.Column("createdTime", "TEXT", false, 0));
        _columnsChatConversationEntity.put("updatedTime", new TableInfo.Column("updatedTime", "TEXT", false, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChatConversationEntity = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesChatConversationEntity = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoChatConversationEntity = new TableInfo("ChatConversationEntity", _columnsChatConversationEntity, _foreignKeysChatConversationEntity, _indicesChatConversationEntity);
        final TableInfo _existingChatConversationEntity = TableInfo.read(_db, "ChatConversationEntity");
        if (! _infoChatConversationEntity.equals(_existingChatConversationEntity)) {
          throw new IllegalStateException("Migration didn't properly handle ChatConversationEntity(com.bykeapk.chatmodule.RoomDB.Entities.ChatConversationEntity).\n"
                  + " Expected:\n" + _infoChatConversationEntity + "\n"
                  + " Found:\n" + _existingChatConversationEntity);
        }
        final HashMap<String, TableInfo.Column> _columnsChatMessageEntity = new HashMap<String, TableInfo.Column>(12);
        _columnsChatMessageEntity.put("id", new TableInfo.Column("id", "INTEGER", true, 1));
        _columnsChatMessageEntity.put("_id", new TableInfo.Column("_id", "TEXT", false, 0));
        _columnsChatMessageEntity.put("uuid", new TableInfo.Column("uuid", "TEXT", false, 0));
        _columnsChatMessageEntity.put("message", new TableInfo.Column("message", "TEXT", false, 0));
        _columnsChatMessageEntity.put("messageType", new TableInfo.Column("messageType", "TEXT", false, 0));
        _columnsChatMessageEntity.put("isRecieved", new TableInfo.Column("isRecieved", "INTEGER", true, 0));
        _columnsChatMessageEntity.put("localFilePath", new TableInfo.Column("localFilePath", "TEXT", false, 0));
        _columnsChatMessageEntity.put("serverFilePath", new TableInfo.Column("serverFilePath", "TEXT", false, 0));
        _columnsChatMessageEntity.put("fileDuration", new TableInfo.Column("fileDuration", "TEXT", false, 0));
        _columnsChatMessageEntity.put("SenderID", new TableInfo.Column("SenderID", "TEXT", false, 0));
        _columnsChatMessageEntity.put("tripID", new TableInfo.Column("tripID", "TEXT", false, 0));
        _columnsChatMessageEntity.put("createdMessageTime", new TableInfo.Column("createdMessageTime", "TEXT", false, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysChatMessageEntity = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysChatMessageEntity.add(new TableInfo.ForeignKey("ChatConversationEntity", "CASCADE", "NO ACTION",Arrays.asList("tripID"), Arrays.asList("trip_id")));
        final HashSet<TableInfo.Index> _indicesChatMessageEntity = new HashSet<TableInfo.Index>(2);
        _indicesChatMessageEntity.add(new TableInfo.Index("index_ChatMessageEntity_tripID", false, Arrays.asList("tripID")));
        _indicesChatMessageEntity.add(new TableInfo.Index("index_ChatMessageEntity__id_uuid", true, Arrays.asList("_id","uuid")));
        final TableInfo _infoChatMessageEntity = new TableInfo("ChatMessageEntity", _columnsChatMessageEntity, _foreignKeysChatMessageEntity, _indicesChatMessageEntity);
        final TableInfo _existingChatMessageEntity = TableInfo.read(_db, "ChatMessageEntity");
        if (! _infoChatMessageEntity.equals(_existingChatMessageEntity)) {
          throw new IllegalStateException("Migration didn't properly handle ChatMessageEntity(com.bykeapk.chatmodule.RoomDB.Entities.ChatMessageEntity).\n"
                  + " Expected:\n" + _infoChatMessageEntity + "\n"
                  + " Found:\n" + _existingChatMessageEntity);
        }
      }
    }, "6a3ed5b5bb76e9a6a28730395c63708a", "439ab32673dbb7d0d1a23a6840647ded");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "ChatConversationEntity","ChatMessageEntity");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `ChatConversationEntity`");
      _db.execSQL("DELETE FROM `ChatMessageEntity`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public ConversationDao chatDao() {
    if (_conversationDao != null) {
      return _conversationDao;
    } else {
      synchronized(this) {
        if(_conversationDao == null) {
          _conversationDao = new ConversationDao_Impl(this);
        }
        return _conversationDao;
      }
    }
  }
}
