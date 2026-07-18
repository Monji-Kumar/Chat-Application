export type Chat = {
  id: number;
  name: string;
  avatar: string;
  type: string;
};

export type Message = {
  id: number;
  chatId: number;
  content: string;
  sender: "me" | "them";
  time: string;
};