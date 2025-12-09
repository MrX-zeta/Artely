const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

/**
 * Función que se activa cuando se escribe un nuevo mensaje en un chat.
 * Ruta: /chats/{chatId}/messages/{messageId}
 */
exports.sendChatNotification = functions.database
    .ref("/chats/{chatId}/messages/{messageId}")
    .onCreate(async (snapshot, context) => {
        const messageData = snapshot.val();
        const chatId = context.params.chatId;

        // 1. Obtener datos del mensaje
        const senderId = messageData.senderId;
        const text = messageData.message || "Nuevo mensaje";
        
        console.log(`Nuevo mensaje en chat ${chatId} de ${senderId}: ${text}`);

        try {
            // 2. Obtener datos del chat para saber quiénes son los participantes
            const chatSnapshot = await admin.database().ref(`/chats/${chatId}`).once('value');
            const chatData = chatSnapshot.val();

            if (!chatData) {
                console.log("Chat no encontrado");
                return null;
            }

            const artistId = chatData.id_Artist;
            const customerId = chatData.id_Customer;

            // 3. Determinar quién es el destinatario
            let recipientId;
            let recipientRole; // "artists" o "customers"

            if (senderId === artistId) {
                recipientId = customerId;
                recipientRole = "customers";
            } else {
                recipientId = artistId;
                recipientRole = "artists";
            }

            // 4. Obtener el token FCM del destinatario
            const recipientSnapshot = await admin.database()
                .ref(`/${recipientRole}/${recipientId}`)
                .once('value');
            
            const recipientData = recipientSnapshot.val();
            
            if (!recipientData || !recipientData.fcmToken) {
                console.log(`El usuario ${recipientId} no tiene token FCM registrado.`);
                return null;
            }

            const fcmToken = recipientData.fcmToken;

            // 5. Preparar la notificación
            const payload = {
                notification: {
                    title: "Nuevo Mensaje",
                    body: text,
                    click_action: "FLUTTER_NOTIFICATION_CLICK" // O la acción que maneje tu intent filter
                },
                data: {
                    chatId: chatId,
                    senderId: senderId,
                    type: "chat_message"
                }
            };

            // 6. Enviar la notificación
            const response = await admin.messaging().sendToDevice(fcmToken, payload);
            console.log("Notificación enviada con éxito:", response);
            
            return null;

        } catch (error) {
            console.error("Error al enviar notificación:", error);
            return null;
        }
    });