import type { Chat, Message } from "../types";

type ChatPanelProps = {
  selectedChat: Chat | undefined;
  messages: Message[];
  messageText: string;
  isLoadingMessages: boolean;
  messageError: string;
  onMessageTextChange: (value: string) => void;
  onSendMessage: (event: React.FormEvent<HTMLFormElement>) => void;
};

function ChatPanel({
  selectedChat,
  messages,
  messageText,
  isLoadingMessages,
  messageError,
  onMessageTextChange,
  onSendMessage
}: ChatPanelProps) {
  return (
    <section className="chat-panel">
      <header className="chat-header">
        <div>
          <h2>{selectedChat?.name ?? "Select a chat"}</h2>
          <p>{selectedChat?.type ?? "No chat selected"}</p>
        </div>
      </header>

      <div className="message-list">
        {isLoadingMessages && (
          <p className="message-status">Loading messages...</p>
        )}

        {messageError && <p className="message-error">{messageError}</p>}

        {!isLoadingMessages && !messageError && messages.length === 0 && (
          <p className="message-status">No messages yet</p>
        )}

        {messages.map((message) => (
          <div
            key={message.id}
            className={
              message.sender === "me"
                ? "message sent"
                : "message received"
            }
          >
            <p>{message.content}</p>
            <span>{message.time}</span>
          </div>
        ))}
      </div>

      <form className="message-composer" onSubmit={onSendMessage}>
        <input
          value={messageText}
          onChange={(event) => onMessageTextChange(event.target.value)}
          placeholder="Type a message..."
          disabled={!selectedChat}
        />
        <button type="submit" disabled={!selectedChat}>
          Send
        </button>
      </form>
    </section>
  );
}

export default ChatPanel;