import React, { useState } from 'react';
import VideoRoom from './components/webrtc/VideoRoom';
import { webRTCService } from './services/webrtc.service';

const App = () => {
    const [roomData, setRoomData] = useState(null);
    const [userData, setUserData] = useState(null);
    const [error, setError] = useState(null);
    const [roomId, setRoomId] = useState(''); 

    const handleCreateRoom = async () => {
        try {
            const roomResponse = await webRTCService.createRoom({
                roomName: `Room-${Date.now()}`,
                maxParticipants: 4
            });

            const userId = `user-${Date.now()}`;
            const username = `User-${userId.substr(-4)}`;

            const joinResponse = await webRTCService.joinRoom(roomResponse.roomId, {
                userId,
                username
            });

            setRoomData(roomResponse);
            setUserData({ userId, username });
        } catch (error) {
            setError(error.message);
        }
    };

    const handleJoinRoom = async (roomId) => {
        try {
            const roomResponse = await webRTCService.getRoom(roomId);
            
            const userId = `user-${Date.now()}`;
            const username = `User-${userId.substr(-4)}`;

            const joinResponse = await webRTCService.joinRoom(roomId, {
                userId,
                username
            });

            setRoomData(roomResponse);
            setUserData({ userId, username });
        } catch (error) {
            setError(error.message);
        }
    };

    const handleLeaveRoom = () => {
        setRoomData(null);
        setUserData(null);
    };

    if (error) {
        return (
            <div className="error-screen">
                <h2>Error</h2>
                <p>{error}</p>
                <button onClick={() => setError(null)}>Try Again</button>
            </div>
        );
    }

    if (!roomData || !userData) {
        return (
            <div className="welcome-screen">
                <h1>WebRTC Video Chat</h1>
                <div className="actions">
                    <button onClick={handleCreateRoom}>
                        Create New Room
                    </button>
                    <div className="join-room">
                        <input
                            type="text"
                            placeholder="Enter Room ID"
                            value={roomId}
                            onChange={(e) => setRoomId(e.target.value)}
                        />
                        <button onClick={() => handleJoinRoom(roomId)}>
                            Join Room
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <VideoRoom
            roomId={roomData.roomId}
            userId={userData.userId}
            username={userData.username}
            onLeave={handleLeaveRoom}
        />
    );
};

export default App;