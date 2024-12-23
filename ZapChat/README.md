# ZapChat

ZapChat is a real-time chat application that allows users to communicate instantly with each other. This project is built using modern web technologies to provide a seamless and responsive chat experience.

## Features

- Real-time messaging
- User authentication
- Group chats
- Media sharing (images, videos)
- Emojis and reactions
- Notifications
- User profiles

## Technologies Used

- Frontend: React, Redux, HTML, CSS
- Backend: Node.js, Express
- Database: MongoDB
- WebSocket: Socket.io
- Authentication: JWT

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/yourusername/ZapChat.git
    ```
2. Navigate to the project directory:
    ```bash
    cd ZapChat
    ```
3. Install dependencies for both frontend and backend:
    ```bash
    cd client
    npm install
    cd ../server
    npm install
    ```
4. Create a `.env` file in the `server` directory and add your environment variables:
    ```env
    MONGO_URI=your_mongodb_uri
    JWT_SECRET=your_jwt_secret
    ```
5. Start the development server:
    ```bash
    cd server
    npm run dev
    ```

## Usage

1. Open your browser and navigate to `http://localhost:3000`.
2. Register a new account or log in with an existing account.
3. Start chatting with your friends!



