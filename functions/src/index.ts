import * as functions from "firebase-functions/v1";
import * as admin from "firebase-admin";

admin.initializeApp();

/**
 * Envía una notificación al artista cuando un customer crea un nuevo chat.
 */
export const sendChatNotification = functions.database
  .ref("/chats/{chatId}")
  .onCreate(async (snapshot, context) => {
    const chatData = snapshot.val();
    const chatId = context.params.chatId;

    const artistId = chatData.artistId;
    const customerId = chatData.customerId;
    const customerName = chatData.customerName || "Un cliente";

    console.log(`Nuevo chat: ${chatId} entre ${customerId} y ${artistId}`);

    try {
      const artistSnapshot = await admin.database()
        .ref(`/users/${artistId}`)
        .once("value");

      const artistData = artistSnapshot.val();

      if (!artistData || !artistData.fcmToken) {
        console.log("Artista sin token FCM");
        return null;
      }

      const message = {
        notification: {
          title: "💬 Nuevo mensaje",
          body: `${customerName} te ha enviado un mensaje`,
        },
        data: {
          chatId: chatId,
          senderId: customerId,
          senderName: customerName,
          type: "new_chat",
        },
        token: artistData.fcmToken,
      };

      const response = await admin.messaging().send(message);
      console.log("Notificación enviada:", response);
      return response;
    } catch (error) {
      console.error("Error enviando notificación:", error);
      return null;
    }
  });

/**
 * Envía una notificación cuando se recibe un nuevo mensaje.
 */
export const sendMessageNotification = functions.database
  .ref("/messages/{chatId}/{messageId}")
  .onCreate(async (snapshot, context) => {
    const messageData = snapshot.val();
    const chatId = context.params.chatId;

    const senderId = messageData.senderId;
    const senderName = messageData.senderName || "Alguien";
    const messageText = messageData.text || "Nuevo mensaje";

    console.log(`Nuevo mensaje en ${chatId} de ${senderName}`);

    try {
      const chatSnapshot = await admin.database()
        .ref(`/chats/${chatId}`)
        .once("value");

      const chatData = chatSnapshot.val();

      if (!chatData) {
        console.log("Chat no encontrado");
        return null;
      }

      const recipientId = senderId === chatData.artistId ?
        chatData.customerId :
        chatData.artistId;

      const recipientSnapshot = await admin.database()
        .ref(`/users/${recipientId}`)
        .once("value");

      const recipientData = recipientSnapshot.val();

      if (!recipientData || !recipientData.fcmToken) {
        console.log("Receptor sin token FCM");
        return null;
      }

      const message = {
        notification: {
          title: `💬 ${senderName}`,
          body: messageText,
        },
        data: {
          chatId: chatId,
          senderId: senderId,
          senderName: senderName,
          type: "new_message",
        },
        token: recipientData.fcmToken,
      };

      const response = await admin.messaging().send(message);
      console.log("Notificación enviada:", response);
      return response;
    } catch (error) {
      console.error("Error enviando notificación:", error);
      return null;
    }
  });
