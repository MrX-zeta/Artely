"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.sendMessageNotification = exports.sendChatNotification = void 0;
const functions = __importStar(require("firebase-functions/v1"));
const admin = __importStar(require("firebase-admin"));
admin.initializeApp();
/**
 * Envía una notificación al artista cuando un customer crea un nuevo chat.
 */
exports.sendChatNotification = functions.database
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
    }
    catch (error) {
        console.error("Error enviando notificación:", error);
        return null;
    }
});
/**
 * Envía una notificación cuando se recibe un nuevo mensaje.
 */
exports.sendMessageNotification = functions.database
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
    }
    catch (error) {
        console.error("Error enviando notificación:", error);
        return null;
    }
});
//# sourceMappingURL=index.js.map