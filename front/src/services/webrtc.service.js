const BASE_URL = 'http://localhost:8080/api/v1/webrtc';

export const webRTCService = {
    createRoom: async (roomData) => {
        try {
            const response = await fetch(`${BASE_URL}/rooms`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(roomData)
            });
            if (!response.ok) throw new Error('방 생성에 실패했습니다.');
            return await response.json();
        } catch (error) {
            console.error('Error creating room:', error);
            throw error;
        }
    },

    joinRoom: async (roomId, userData) => {
        try {
            const response = await fetch(`${BASE_URL}/rooms/${roomId}/join`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(userData)
            });
            if (!response.ok) throw new Error('방 참여에 실패했습니다.');
            return await response.json();
        } catch (error) {
            console.error('Error joining room:', error);
            throw error;
        }
    },

    getRooms: async () => {
        try {
            const response = await fetch(`${BASE_URL}/rooms`);
            if (!response.ok) throw new Error('방 목록 조회에 실패했습니다.');
            return await response.json();
        } catch (error) {
            console.error('Error getting rooms:', error);
            throw error;
        }
    },

    getRoom: async (roomId) => {
        try {
            const response = await fetch(`${BASE_URL}/rooms/${roomId}`);
            if (!response.ok) throw new Error('방 정보 조회에 실패했습니다.');
            return await response.json();
        } catch (error) {
            console.error('Error getting room:', error);
            throw error;
        }
    }
};