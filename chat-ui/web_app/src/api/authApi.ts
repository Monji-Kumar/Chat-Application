import { apiRequest } from "./client";

type ApiResponse<T> = {
  success: boolean;
  message: string;
  data: T;
};

type LoginRequest = {
  username: string;
  password: string;
};

type LoginData = {
  username: string;
  role: string;
  message: string;
};

type RegisterRequest = {
  name: string;
  email: string;
  username: string;
  password: string;
  phone: string;
};

type RegisterData = unknown;

export async function loginUser(request: LoginRequest) {
  const response = await apiRequest<ApiResponse<LoginData>>("/api/auth/login", {
    method: "POST",
    body: request
  });

  return response.data;
}

export async function registerUser(request: RegisterRequest) {
  const response = await apiRequest<ApiResponse<RegisterData>>(
    "/api/auth/register",
    {
      method: "POST",
      body: request
    }
  );

  return response.data;
}

export function logoutUser() {
  return apiRequest<unknown>("/api/auth/logout", {
    method: "POST"
  });
}