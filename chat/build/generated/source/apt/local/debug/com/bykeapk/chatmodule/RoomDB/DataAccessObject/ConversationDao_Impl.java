package com.bykeapk.chatmodule.RoomDB.DataAccessObject;

import android.arch.persistence.db.SupportSQLiteStatement;
import android.arch.persistence.room.EntityInsertionAdapter;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomSQLiteQuery;
import android.arch.persistence.room.SharedSQLiteStatement;
import android.arch.persistence.room.util.StringUtil;
import android.database.Cursor;
import com.bykeapk.chatmodule.RoomDB.Entities.ChatConversationEntity;
import com.bykeapk.chatmodule.RoomDB.Entities.ChatMessageEntity;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class ConversationDao_Impl implements ConversationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfChatConversationEntity;

  private final EntityInsertionAdapter __insertionAdapterOfChatMessageEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateConversation;

  private final SharedSQLiteStatement __preparedStmtOfUpdateServerAndLocalPath;

  private final SharedSQLiteStatement __preparedStmtOfUpdateServerAndLocalPathWithID;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMessages;

  private final SharedSQLiteStatement __preparedStmtOfDeleteConversation;

  private final SharedSQLiteStatement __preparedStmtOfUpdateMessageID;

  private final SharedSQLiteStatement __preparedStmtOfUpdateMessageIDAndServerFile;

  public ConversationDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfChatConversationEntity = new EntityInsertionAdapter<ChatConversationEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `ChatConversationEntity`(`trip_id`,`conversationID`,`recieverID`,`createdTime`,`updatedTime`) VALUES (?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ChatConversationEntity value) {
        if (value.getTripID() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getTripID());
        }
        if (value.getConversationID() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getConversationID());
        }
        if (value.getRecieverID() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getRecieverID());
        }
        if (value.getCreatedTime() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getCreatedTime());
        }
        if (value.getUpdatedTime() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getUpdatedTime());
        }
      }
    };
    this.__insertionAdapterOfChatMessageEntity = new EntityInsertionAdapter<ChatMessageEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `ChatMessageEntity`(`id`,`_id`,`uuid`,`message`,`messageType`,`isRecieved`,`localFilePath`,`serverFilePath`,`fileDuration`,`SenderID`,`tripID`,`createdMessageTime`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, ChatMessageEntity value) {
        stmt.bindLong(1, value.getId());
        if (value.get_id() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.get_id());
        }
        if (value.getUuid() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getUuid());
        }
        if (value.getMessage() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getMessage());
        }
        if (value.getMessageType() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getMessageType());
        }
        final int _tmp;
        _tmp = value.isReceived() ? 1 : 0;
        stmt.bindLong(6, _tmp);
        if (value.getLocalFilePath() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getLocalFilePath());
        }
        if (value.getServerFilePath() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getServerFilePath());
        }
        if (value.getFileDuration() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getFileDuration());
        }
        if (value.getSenderID() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getSenderID());
        }
        if (value.getTripID() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getTripID());
        }
        if (value.getCreatedTime() == null) {
          stmt.bindNull(12);
        } else {
          stmt.bindString(12, value.getCreatedTime());
        }
      }
    };
    this.__preparedStmtOfUpdateConversation = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE chatconversationentity SET updatedTime=?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateServerAndLocalPath = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE chatmessageentity SET serverFilePath=?, localFilePath=? where uuid=?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateServerAndLocalPathWithID = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE chatmessageentity SET serverFilePath=?, localFilePath=? where _id=?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteMessages = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM chatmessageentity where tripID=?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteConversation = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM chatconversationentity where trip_id=?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateMessageID = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE chatmessageentity SET _id=? where uuid=?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateMessageIDAndServerFile = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE chatmessageentity SET _id=?, serverFilePath=? where uuid=?";
        return _query;
      }
    };
  }

  @Override
  public void insertConversation(ChatConversationEntity conversation) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfChatConversationEntity.insert(conversation);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public long insertMessage(ChatMessageEntity message) {
    __db.beginTransaction();
    try {
      long _result = __insertionAdapterOfChatMessageEntity.insertAndReturnId(message);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateConversation(String updatedTime) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateConversation.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (updatedTime == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, updatedTime);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdateConversation.release(_stmt);
    }
  }

  @Override
  public long updateServerAndLocalPath(String serverUrl, String localPath, String UUID) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateServerAndLocalPath.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (serverUrl == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, serverUrl);
      }
      _argIndex = 2;
      if (localPath == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, localPath);
      }
      _argIndex = 3;
      if (UUID == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, UUID);
      }
      final long _result = _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdateServerAndLocalPath.release(_stmt);
    }
  }

  @Override
  public long updateServerAndLocalPathWithID(String serverUrl, String localPath, String id) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateServerAndLocalPathWithID.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (serverUrl == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, serverUrl);
      }
      _argIndex = 2;
      if (localPath == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, localPath);
      }
      _argIndex = 3;
      if (id == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, id);
      }
      final long _result = _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdateServerAndLocalPathWithID.release(_stmt);
    }
  }

  @Override
  public void deleteMessages(String tripID) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMessages.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (tripID == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, tripID);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteMessages.release(_stmt);
    }
  }

  @Override
  public void deleteConversation(String tripID) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteConversation.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (tripID == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, tripID);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfDeleteConversation.release(_stmt);
    }
  }

  @Override
  public void updateMessageID(String id, String UUID) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateMessageID.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (id == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, id);
      }
      _argIndex = 2;
      if (UUID == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, UUID);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdateMessageID.release(_stmt);
    }
  }

  @Override
  public void updateMessageIDAndServerFile(String id, String serverFilePath, String UUID) {
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateMessageIDAndServerFile.acquire();
    __db.beginTransaction();
    try {
      int _argIndex = 1;
      if (id == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, id);
      }
      _argIndex = 2;
      if (serverFilePath == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, serverFilePath);
      }
      _argIndex = 3;
      if (UUID == null) {
        _stmt.bindNull(_argIndex);
      } else {
        _stmt.bindString(_argIndex, UUID);
      }
      _stmt.executeUpdateDelete();
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
      __preparedStmtOfUpdateMessageIDAndServerFile.release(_stmt);
    }
  }

  @Override
  public List<ChatMessageEntity> getConversationMessages(String tripID) {
    final String _sql = "select * from chatmessageentity m JOIN chatconversationentity c ON m.tripID = c.trip_id where m.tripID=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (tripID == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, tripID);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
      final int _cursorIndexOfId_1 = _cursor.getColumnIndexOrThrow("_id");
      final int _cursorIndexOfUuid = _cursor.getColumnIndexOrThrow("uuid");
      final int _cursorIndexOfMessage = _cursor.getColumnIndexOrThrow("message");
      final int _cursorIndexOfMessageType = _cursor.getColumnIndexOrThrow("messageType");
      final int _cursorIndexOfIsReceived = _cursor.getColumnIndexOrThrow("isRecieved");
      final int _cursorIndexOfLocalFilePath = _cursor.getColumnIndexOrThrow("localFilePath");
      final int _cursorIndexOfServerFilePath = _cursor.getColumnIndexOrThrow("serverFilePath");
      final int _cursorIndexOfFileDuration = _cursor.getColumnIndexOrThrow("fileDuration");
      final int _cursorIndexOfSenderID = _cursor.getColumnIndexOrThrow("SenderID");
      final int _cursorIndexOfTripID = _cursor.getColumnIndexOrThrow("tripID");
      final int _cursorIndexOfCreatedTime = _cursor.getColumnIndexOrThrow("createdMessageTime");
      final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ChatMessageEntity _item;
        final String _tmpUuid;
        _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
        final String _tmpMessage;
        _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
        final String _tmpMessageType;
        _tmpMessageType = _cursor.getString(_cursorIndexOfMessageType);
        final boolean _tmpIsReceived;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsReceived);
        _tmpIsReceived = _tmp != 0;
        final String _tmpLocalFilePath;
        _tmpLocalFilePath = _cursor.getString(_cursorIndexOfLocalFilePath);
        final String _tmpServerFilePath;
        _tmpServerFilePath = _cursor.getString(_cursorIndexOfServerFilePath);
        final String _tmpFileDuration;
        _tmpFileDuration = _cursor.getString(_cursorIndexOfFileDuration);
        final String _tmpSenderID;
        _tmpSenderID = _cursor.getString(_cursorIndexOfSenderID);
        final String _tmpTripID;
        _tmpTripID = _cursor.getString(_cursorIndexOfTripID);
        final String _tmpCreatedTime;
        _tmpCreatedTime = _cursor.getString(_cursorIndexOfCreatedTime);
        _item = new ChatMessageEntity(_tmpUuid,_tmpMessage,_tmpMessageType,_tmpIsReceived,_tmpLocalFilePath,_tmpServerFilePath,_tmpFileDuration,_tmpSenderID,_tmpTripID,_tmpCreatedTime);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final String _tmp_id;
        _tmp_id = _cursor.getString(_cursorIndexOfId_1);
        _item.set_id(_tmp_id);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public ChatConversationEntity getConversation(String conversationID) {
    final String _sql = "select * from chatconversationentity where conversationID=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (conversationID == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, conversationID);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfTripID = _cursor.getColumnIndexOrThrow("trip_id");
      final int _cursorIndexOfConversationID = _cursor.getColumnIndexOrThrow("conversationID");
      final int _cursorIndexOfRecieverID = _cursor.getColumnIndexOrThrow("recieverID");
      final int _cursorIndexOfCreatedTime = _cursor.getColumnIndexOrThrow("createdTime");
      final int _cursorIndexOfUpdatedTime = _cursor.getColumnIndexOrThrow("updatedTime");
      final ChatConversationEntity _result;
      if(_cursor.moveToFirst()) {
        final String _tmpTripID;
        _tmpTripID = _cursor.getString(_cursorIndexOfTripID);
        final String _tmpConversationID;
        _tmpConversationID = _cursor.getString(_cursorIndexOfConversationID);
        final String _tmpRecieverID;
        _tmpRecieverID = _cursor.getString(_cursorIndexOfRecieverID);
        final String _tmpCreatedTime;
        _tmpCreatedTime = _cursor.getString(_cursorIndexOfCreatedTime);
        final String _tmpUpdatedTime;
        _tmpUpdatedTime = _cursor.getString(_cursorIndexOfUpdatedTime);
        _result = new ChatConversationEntity(_tmpConversationID,_tmpTripID,_tmpRecieverID,_tmpCreatedTime,_tmpUpdatedTime);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int isMessageFound(String id) {
    final String _sql = "SELECT COUNT(_id) FROM chatmessageentity where _id=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (id == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, id);
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ChatMessageEntity> getMessageRow(List<String> uuidList, List<String> idList) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM chatmessageentity where uuid IN(");
    final int _inputSize = uuidList.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") AND _id IN(");
    final int _inputSize_1 = idList.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize_1);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize + _inputSize_1;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : uuidList) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindString(_argIndex, _item);
      }
      _argIndex ++;
    }
    _argIndex = 1 + _inputSize;
    for (String _item_1 : idList) {
      if (_item_1 == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindString(_argIndex, _item_1);
      }
      _argIndex ++;
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
      final int _cursorIndexOfId_1 = _cursor.getColumnIndexOrThrow("_id");
      final int _cursorIndexOfUuid = _cursor.getColumnIndexOrThrow("uuid");
      final int _cursorIndexOfMessage = _cursor.getColumnIndexOrThrow("message");
      final int _cursorIndexOfMessageType = _cursor.getColumnIndexOrThrow("messageType");
      final int _cursorIndexOfIsReceived = _cursor.getColumnIndexOrThrow("isRecieved");
      final int _cursorIndexOfLocalFilePath = _cursor.getColumnIndexOrThrow("localFilePath");
      final int _cursorIndexOfServerFilePath = _cursor.getColumnIndexOrThrow("serverFilePath");
      final int _cursorIndexOfFileDuration = _cursor.getColumnIndexOrThrow("fileDuration");
      final int _cursorIndexOfSenderID = _cursor.getColumnIndexOrThrow("SenderID");
      final int _cursorIndexOfTripID = _cursor.getColumnIndexOrThrow("tripID");
      final int _cursorIndexOfCreatedTime = _cursor.getColumnIndexOrThrow("createdMessageTime");
      final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ChatMessageEntity _item_2;
        final String _tmpUuid;
        _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
        final String _tmpMessage;
        _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
        final String _tmpMessageType;
        _tmpMessageType = _cursor.getString(_cursorIndexOfMessageType);
        final boolean _tmpIsReceived;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsReceived);
        _tmpIsReceived = _tmp != 0;
        final String _tmpLocalFilePath;
        _tmpLocalFilePath = _cursor.getString(_cursorIndexOfLocalFilePath);
        final String _tmpServerFilePath;
        _tmpServerFilePath = _cursor.getString(_cursorIndexOfServerFilePath);
        final String _tmpFileDuration;
        _tmpFileDuration = _cursor.getString(_cursorIndexOfFileDuration);
        final String _tmpSenderID;
        _tmpSenderID = _cursor.getString(_cursorIndexOfSenderID);
        final String _tmpTripID;
        _tmpTripID = _cursor.getString(_cursorIndexOfTripID);
        final String _tmpCreatedTime;
        _tmpCreatedTime = _cursor.getString(_cursorIndexOfCreatedTime);
        _item_2 = new ChatMessageEntity(_tmpUuid,_tmpMessage,_tmpMessageType,_tmpIsReceived,_tmpLocalFilePath,_tmpServerFilePath,_tmpFileDuration,_tmpSenderID,_tmpTripID,_tmpCreatedTime);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item_2.setId(_tmpId);
        final String _tmp_id;
        _tmp_id = _cursor.getString(_cursorIndexOfId_1);
        _item_2.set_id(_tmp_id);
        _result.add(_item_2);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<ChatMessageEntity> getMessageRow(List<String> uuid) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM chatmessageentity where uuid IN (");
    final int _inputSize = uuid.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : uuid) {
      if (_item == null) {
        _statement.bindNull(_argIndex);
      } else {
        _statement.bindString(_argIndex, _item);
      }
      _argIndex ++;
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfId = _cursor.getColumnIndexOrThrow("id");
      final int _cursorIndexOfId_1 = _cursor.getColumnIndexOrThrow("_id");
      final int _cursorIndexOfUuid = _cursor.getColumnIndexOrThrow("uuid");
      final int _cursorIndexOfMessage = _cursor.getColumnIndexOrThrow("message");
      final int _cursorIndexOfMessageType = _cursor.getColumnIndexOrThrow("messageType");
      final int _cursorIndexOfIsReceived = _cursor.getColumnIndexOrThrow("isRecieved");
      final int _cursorIndexOfLocalFilePath = _cursor.getColumnIndexOrThrow("localFilePath");
      final int _cursorIndexOfServerFilePath = _cursor.getColumnIndexOrThrow("serverFilePath");
      final int _cursorIndexOfFileDuration = _cursor.getColumnIndexOrThrow("fileDuration");
      final int _cursorIndexOfSenderID = _cursor.getColumnIndexOrThrow("SenderID");
      final int _cursorIndexOfTripID = _cursor.getColumnIndexOrThrow("tripID");
      final int _cursorIndexOfCreatedTime = _cursor.getColumnIndexOrThrow("createdMessageTime");
      final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final ChatMessageEntity _item_1;
        final String _tmpUuid;
        _tmpUuid = _cursor.getString(_cursorIndexOfUuid);
        final String _tmpMessage;
        _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
        final String _tmpMessageType;
        _tmpMessageType = _cursor.getString(_cursorIndexOfMessageType);
        final boolean _tmpIsReceived;
        final int _tmp;
        _tmp = _cursor.getInt(_cursorIndexOfIsReceived);
        _tmpIsReceived = _tmp != 0;
        final String _tmpLocalFilePath;
        _tmpLocalFilePath = _cursor.getString(_cursorIndexOfLocalFilePath);
        final String _tmpServerFilePath;
        _tmpServerFilePath = _cursor.getString(_cursorIndexOfServerFilePath);
        final String _tmpFileDuration;
        _tmpFileDuration = _cursor.getString(_cursorIndexOfFileDuration);
        final String _tmpSenderID;
        _tmpSenderID = _cursor.getString(_cursorIndexOfSenderID);
        final String _tmpTripID;
        _tmpTripID = _cursor.getString(_cursorIndexOfTripID);
        final String _tmpCreatedTime;
        _tmpCreatedTime = _cursor.getString(_cursorIndexOfCreatedTime);
        _item_1 = new ChatMessageEntity(_tmpUuid,_tmpMessage,_tmpMessageType,_tmpIsReceived,_tmpLocalFilePath,_tmpServerFilePath,_tmpFileDuration,_tmpSenderID,_tmpTripID,_tmpCreatedTime);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item_1.setId(_tmpId);
        final String _tmp_id;
        _tmp_id = _cursor.getString(_cursorIndexOfId_1);
        _item_1.set_id(_tmp_id);
        _result.add(_item_1);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
