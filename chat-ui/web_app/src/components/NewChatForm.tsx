import { useState } from "react";

type NewChatFormProps = {
  onCancel: () => void;
  onCreate: (values: {
    targetAuthUserId: string;
    targetUsername: string;
    targetDisplayName: string;
    targetAvatarUrl: string;
  }) => void;
  isCreating: boolean;
};

function NewChatForm({ onCancel, onCreate, isCreating }: NewChatFormProps) {
  const [targetAuthUserId, setTargetAuthUserId] = useState("");
  const [targetUsername, setTargetUsername] = useState("");
  const [targetDisplayName, setTargetDisplayName] = useState("");

  function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (
      targetAuthUserId.trim().length === 0 ||
      targetUsername.trim().length === 0 ||
      targetDisplayName.trim().length === 0
    ) {
      return;
    }

    onCreate({
      targetAuthUserId: targetAuthUserId.trim(),
      targetUsername: targetUsername.trim(),
      targetDisplayName: targetDisplayName.trim(),
      targetAvatarUrl: ""
    });
  }

  return (
    <div className="modal-backdrop">
      <section className="modal">
        <header className="modal-header">
          <h2>New Direct Chat</h2>
          <button type="button" onClick={onCancel}>
            Close
          </button>
        </header>

        <form className="modal-form" onSubmit={handleSubmit}>
          <label>
            Target User ID
            <input
              value={targetAuthUserId}
              onChange={(event) => setTargetAuthUserId(event.target.value)}
              placeholder="Example: 2"
            />
          </label>

          <label>
            Username
            <input
              value={targetUsername}
              onChange={(event) => setTargetUsername(event.target.value)}
              placeholder="Example: sana"
            />
          </label>

          <label>
            Display Name
            <input
              value={targetDisplayName}
              onChange={(event) => setTargetDisplayName(event.target.value)}
              placeholder="Example: Sana"
            />
          </label>

          <button type="submit" disabled={isCreating}>
            {isCreating ? "Creating..." : "Create Chat"}
          </button>
        </form>
      </section>
    </div>
  );
}

export default NewChatForm;