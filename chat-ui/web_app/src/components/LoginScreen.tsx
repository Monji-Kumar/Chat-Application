import { useState } from "react";

type LoginFormValues = {
  username: string;
  password: string;
};

type LoginScreenProps = {
  onLogin: (values: LoginFormValues) => void;
  errorMessage: string;
  isLoading: boolean;
};

function LoginScreen({ onLogin, errorMessage, isLoading }: LoginScreenProps) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const trimmedUsername = username.trim();

    if (trimmedUsername.length === 0 || password.length === 0) {
      return;
    }

    onLogin({
      username: trimmedUsername,
      password
    });
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <div className="brand auth-brand">
          <div className="brand-mark">M</div>
          <div>
            <h1>Monji</h1>
            <p>Sign in to continue</p>
          </div>
        </div>

        <form className="auth-form" onSubmit={handleSubmit}>
          <label>
            Username
            <input
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              placeholder="Enter username"
            />
          </label>

          <label>
            Password
            <input
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              placeholder="Enter password"
              type="password"
            />
          </label>

          {errorMessage && <p className="form-error">{errorMessage}</p>}

          <button type="submit" disabled={isLoading}>
            {isLoading ? "Logging in..." : "Login"}
          </button>
        </form>
      </section>
    </main>
  );
}

export default LoginScreen;