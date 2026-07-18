import { apiRequest } from "./client";

export type BackendChatRoom = {
  id: number;
  name: string | null;
  type: string;
};

type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
};

export async function getMyChats() {
  const response = await apiRequest<ApiResponse<BackendChatRoom[]>>("/api/chat");

  return response.data;
}

export type CreateDirectChatRequest = {
  targetAuthUserId: string;
  targetUsername: string;
  targetDisplayName: string;
  targetAvatarUrl: string;
};

export async function createDirectChat(request: CreateDirectChatRequest) {
  const response = await apiRequest<ApiResponse<BackendChatRoom>>(
    "/api/chat/direct",
    {
      method: "POST",
      body: request
    }
  );

  return response.data;
}

export type BackendMessage = {
  id: number;
  chatRoomId: number;
  senderAuthUserId: string;
  senderUsername: string;
  content: string;
  messageType: string;
  createdAt: string;
};

export async function getChatMessages(chatRoomId: number) {
  return apiRequest<BackendMessage[]>(
    `/api/chat/by-room-id/messages?chatRoomId=${chatRoomId}`
  );
}

export type SendMessageRequest = {
  content: string;
  messageType: "TEXT" | "IMAGE" | "FILE" | "SYSTEM" | "VIDEO";
};

export async function sendChatMessage(
  chatRoomId: number,
  request: SendMessageRequest
) {
  const response = await apiRequest<ApiResponse<BackendMessage>>(
    `/api/chat/by-room-id/messages?chatRoomId=${chatRoomId}`,
    {
      method: "POST",
      body: request
    }
  );

  return response.data;
}