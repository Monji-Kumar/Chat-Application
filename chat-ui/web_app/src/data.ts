import type { Chat, Message } from "./types";

export const chats: Chat[] = [
  {
    id: 1,
    name: "Aarav",
    avatar: "A",
    type: "Direct message"
  },
  {
    id: 2,
    name: "Sana",
    avatar: "S",
    type: "Direct message"
  }
];

export const initialMessages: Message[] = [
  {
    id: 1,
    chatId: 1,
    content: "Hey, are you there?",
    sender: "them",
    time: "10:24"
  },
  {
    id: 2,
    chatId: 1,
    content: "Yes, building the UI now.",
    sender: "me",
    time: "10:25"
  },
  {
    id: 3,
    chatId: 2,
    content: "Let’s test the app.",
    sender: "them",
    time: "10:30"
  }
];