import type { Chat } from "../types";

type SidebarProps = {
  chats: Chat[];
  selectedChatId: number;
  currentUsername: string;
  isLoadingChats: boolean;
  chatError: string;
  onSelectChat: (chatId: number) => void;
  onOpenNewChat: () => void;
  onLogout: () => void;
  getLatestMessage: (chatId: number) => string;
};

function Sidebar({
  chats,
  selectedChatId,
  currentUsername,
  isLoadingChats,
  chatError,
  onSelectChat,
  onOpenNewChat,
  onLogout,
  getLatestMessage
}: SidebarProps) {
  return (
    <aside className="sidebar">
      <div className="brand">
        <div className="brand-mark">M</div>
        <div>
          <h1>Monji</h1>
          <p>Signed in as {currentUsername}</p>
        </div>
      </div>

      <button
        type="button"
        className="new-chat-button"
        onClick={onOpenNewChat}
      >
        New Chat
      </button>

      <div className="chat-list">
        {isLoadingChats && <p className="sidebar-status">Loading chats...</p>}

        {chatError && <p className="sidebar-error">{chatError}</p>}

        {!isLoadingChats && !chatError && chats.length === 0 && (
          <p className="sidebar-status">No chats yet</p>
        )}

        {chats.map((chat) => (
          <button
            key={chat.id}
            type="button"
            className={
              chat.id === selectedChatId
                ? "chat-list-item active"
                : "chat-list-item"
            }
            onClick={() => onSelectChat(chat.id)}
          >
            <span className="avatar">{chat.avatar}</span>
            <span>
              <strong>{chat.name}</strong>
              <small>{getLatestMessage(chat.id)}</small>
            </span>
          </button>
        ))}
      </div>

      <button type="button" className="logout-button" onClick={onLogout}>
        Logout
      </button>
    </aside>
  );
}

export default Sidebar;