import { useEffect, useRef, useState } from "react";
import Sidebar from "./components/Sidebar";
import ChatPanel from "./components/ChatPanel";
import LoginScreen from "./components/LoginScreen";
import { loginUser, logoutUser } from "./api/authApi";
import type { Chat, Message } from "./types";
import NewChatForm from "./components/NewChatForm";
import { createDirectChat, getChatMessages, getMyChats, sendChatMessage } from "./api/chatApi";
import type { Client } from "@stomp/stompjs";
import { createSocketClient } from "./api/socketClient";

function App() {
  const [currentUsername, setCurrentUsername] = useState<string | null>(() => {
    return localStorage.getItem("monji_username");
  });

  const [authError, setAuthError] = useState("");
  const [isLoggingIn, setIsLoggingIn] = useState(false);

  const [chats, setChats] = useState<Chat[]>([]);
  const [isLoadingChats, setIsLoadingChats] = useState(false);
  const [chatError, setChatError] = useState("");

  const [selectedChatId, setSelectedChatId] = useState(1);
  const [messages, setMessages] = useState<Message[]>([]);
  const [isLoadingMessages, setIsLoadingMessages] = useState(false);
  const [messageError, setMessageError] = useState("");
  const [messageText, setMessageText] = useState("");

  const [isNewChatOpen, setIsNewChatOpen] = useState(false);
  const [isCreatingChat, setIsCreatingChat] = useState(false);

  const selectedChat = chats.find((chat) => chat.id === selectedChatId);
  const socketClientRef = useRef<Client | null>(null);

  const visibleMessages = messages.filter(
    (message) => message.chatId === selectedChatId
  );

  useEffect(() => {
    if (!currentUsername) {
      return;
    }

    async function loadChats() {
      try {
        console.log("Loading chats...");

        setIsLoadingChats(true);
        setChatError("");

        const backendChats = await getMyChats();

        console.log("Backend chats:", backendChats);

        const mappedChats: Chat[] = backendChats.map((chat) => ({
          id: chat.id,
          name: chat.name ?? `Chat ${chat.id}`,
          avatar: (chat.name ?? "C").charAt(0).toUpperCase(),
          type: chat.type
        }));

        setChats(mappedChats);

        if (mappedChats.length > 0) {
          setSelectedChatId(mappedChats[0].id);
        }
      } catch (error) {
        console.error(error);
        setChatError("Could not load chats");
      } finally {
        setIsLoadingChats(false);
      }
    }

    loadChats();
  }, [currentUsername]);

  useEffect(() => {
  if (!currentUsername) {
    return;
  }

  const client = createSocketClient();

  client.onConnect = () => {
    console.log("WebSocket connected");
  };

  client.onStompError = (frame) => {
    console.error("STOMP error:", frame.headers.message);
    console.error(frame.body);
  };

  client.onWebSocketError = (event) => {
    console.error("WebSocket error:", event);
  };

  client.activate();
  socketClientRef.current = client;

  return () => {
    client.deactivate();
    socketClientRef.current = null;
  };
}, [currentUsername]);

  function formatMessageTime(value: string | null | undefined) {
  if (!value) {
    return "";
  }

  const utcValue = value.endsWith("Z") ? value : `${value}Z`;
  const date = new Date(utcValue);

  if (Number.isNaN(date.getTime())) {
    return "";
  }

  return date.toLocaleTimeString([], {
    hour: "2-digit",
    minute: "2-digit"
  });
}

useEffect(() => {
  const client = socketClientRef.current;

  if (!client || !client.connected || !selectedChatId) {
    return;
  }

  const subscription = client.subscribe(
    `/topic/chats/${selectedChatId}`,
    (message) => {
      const body = JSON.parse(message.body);

      const newMessage: Message = {
        id: body.id,
        chatId: selectedChatId,
        content: body.content,
        sender: body.senderUsername === currentUsername ? "me" : "them",
        time: formatMessageTime(body.createdAt)
      };

      setMessages((currentMessages) => {
        const alreadyExists = currentMessages.some(
          (existingMessage) => existingMessage.id === newMessage.id
        );

        if (alreadyExists) {
          return currentMessages;
        }

        return [...currentMessages, newMessage];
      });
    }
  );

  return () => {
    subscription.unsubscribe();
  };
}, [selectedChatId, currentUsername]);

  useEffect(() => {
  if (!currentUsername || chats.length === 0 || !selectedChatId) {
    return;
  }

  async function loadMessages() {
    try {
      setIsLoadingMessages(true);
      setMessageError("");

      const backendMessages = await getChatMessages(selectedChatId);

      const mappedMessages: Message[] = backendMessages.map((message) => ({
        id: message.id,
        chatId: selectedChatId,
        content: message.content,
        sender:
          message.senderUsername === currentUsername
            ? "me"
            : "them",
        time: formatMessageTime(message.createdAt)
      }));

      setMessages(mappedMessages);
    } catch (error) {
      console.error(error);
      setMessageError("Could not load messages");
      setMessages([]);
    } finally {
      setIsLoadingMessages(false);
    }
  }

  loadMessages();
}, [currentUsername, chats.length, selectedChatId]);

  function getLatestMessage(chatId: number) {
    const chatMessages = messages.filter((message) => message.chatId === chatId);
    const latestMessage = chatMessages[chatMessages.length - 1];

    if (!latestMessage) {
      return "No messages yet";
    }

    return latestMessage.content;
  }

  async function handleLogin(values: { username: string; password: string }) {
    try {
      setIsLoggingIn(true);
      setAuthError("");

      await loginUser({
        username: values.username,
        password: values.password
      });

      localStorage.setItem("monji_username", values.username);
      setCurrentUsername(values.username);
    } catch (error) {
      console.error(error);
      setAuthError("Invalid username or password");
    } finally {
      setIsLoggingIn(false);
    }
  }

  async function handleLogout() {
    try {
      await logoutUser();
    } finally {
      localStorage.removeItem("monji_username");
      setCurrentUsername(null);
      setChats([]);
    }
  }

  async function handleCreateDirectChat(values: {
  targetAuthUserId: string;
  targetUsername: string;
  targetDisplayName: string;
  targetAvatarUrl: string;
}) {
  try {
    setIsCreatingChat(true);

    const backendChat = await createDirectChat(values);

    const newChat = {
      id: backendChat.id,
      name: backendChat.name ?? values.targetDisplayName,
      avatar: (backendChat.name ?? values.targetDisplayName).charAt(0).toUpperCase(),
      type: backendChat.type
    };

    setChats((currentChats) => {
      const alreadyExists = currentChats.some((chat) => chat.id === newChat.id);

      if (alreadyExists) {
        return currentChats;
      }

      return [...currentChats, newChat];
    });

    setSelectedChatId(newChat.id);
    setIsNewChatOpen(false);
  } catch (error) {
    console.error(error);
    alert("Could not create chat");
  } finally {
    setIsCreatingChat(false);
  }
}

  function handleSendMessage(event: React.FormEvent<HTMLFormElement>) {
  event.preventDefault();

  const trimmedMessage = messageText.trim();
  const client = socketClientRef.current;

  if (trimmedMessage.length === 0 || !selectedChat || !client?.connected) {
    return;
  }

  client.publish({
    destination: "/app/chat/send",
    body: JSON.stringify({
      chatRoomId: selectedChat.id,
      content: trimmedMessage,
      type: "TEXT"
    })
  });

  setMessageText("");
}

  if (!currentUsername) {
    return (
      <LoginScreen
        onLogin={handleLogin}
        errorMessage={authError}
        isLoading={isLoggingIn}
      />
    );
  }

  return (
    <main className="app-shell">
      <Sidebar
        chats={chats}
        selectedChatId={selectedChatId}
        currentUsername={currentUsername}
        isLoadingChats={isLoadingChats}
        chatError={chatError}
        onSelectChat={setSelectedChatId}
        onLogout={handleLogout}
        getLatestMessage={getLatestMessage}
        onOpenNewChat={() => setIsNewChatOpen(true)}
      />

      <ChatPanel
        selectedChat={selectedChat}
        messages={visibleMessages}
        messageText={messageText}
        onMessageTextChange={setMessageText}
        onSendMessage={handleSendMessage}
        isLoadingMessages={isLoadingMessages}
        messageError={messageError}
      />

      {isNewChatOpen && (
    <NewChatForm
        onCancel={() => setIsNewChatOpen(false)}
        onCreate={handleCreateDirectChat}
        isCreating={isCreatingChat}
      />
    )}
    </main>
  );
}

export default App;