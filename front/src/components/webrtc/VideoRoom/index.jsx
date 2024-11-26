import React, {useState, useCallback, useEffect} from 'react';
import VideoPlayer from '../VideoPlayer';
import Controls from '../Controls';
import ParticipantList from '../ParticipantList';
import {useWebRTC} from '../../../hooks/useWebRTC';
import './style.css';

const VideoRoom = ({roomId, userId, username, onLeave}) => {
    const [isAudioEnabled, setIsAudioEnabled] = useState(true);
    const [isVideoEnabled, setIsVideoEnabled] = useState(true);
    const [isScreenSharing, setIsScreenSharing] = useState(false);

    const {
        localStream,
        remoteStreams,
        isConnected,
        error,
        initialize,
        cleanup
    } = useWebRTC(roomId, userId);

    useEffect(() => {
        console.log("VideoRoom mounted - initializing");
        initialize();

        return () => {
            console.log("VideoRoom unmounting - cleaning up");
            cleanup();
        };
    }, [roomId, userId]);

    const handleToggleAudio = useCallback(() => {
        if (localStream) {
            localStream.getAudioTracks().forEach(track => {
                track.enabled = !track.enabled;
            });
            setIsAudioEnabled(prev => !prev);
        }
    }, [localStream]);

    const handleToggleVideo = useCallback(() => {
        if (localStream) {
            localStream.getVideoTracks().forEach(track => {
                track.enabled = !track.enabled;
            });
            setIsVideoEnabled(prev => !prev);
        }
    }, [localStream]);

    const handleToggleScreenShare = useCallback(async () => {
        try {
            if (isScreenSharing) {
                await initialize();
                setIsScreenSharing(false);
            } else {
                const screenStream = await navigator.mediaDevices.getDisplayMedia({
                    video: true
                });
                screenStream.getVideoTracks()[0].onended = () => {
                    handleToggleScreenShare();
                };
                setIsScreenSharing(true);
            }
        } catch (error) {
            console.error('Error toggling screen share:', error);
        }
    }, [isScreenSharing, initialize]);

    const handleLeaveRoom = useCallback(() => {
        cleanup();
        onLeave();
    }, [cleanup, onLeave]);

    const participants = [
        {
            userId,
            username,
            isLocal: true,
            isConnected: true
        },
        ...Array.from(remoteStreams.entries()).map(([remoteUserId, stream]) => ({
            userId: remoteUserId,
            username: `User-${remoteUserId}`,
            isLocal: false,
            isConnected: true
        }))
    ];

    if (error) {
        return (
            <div className="error-container">
                <h2>Error</h2>
                <p>{error}</p>
                <button onClick={handleLeaveRoom}>Leave Room</button>
            </div>
        );
    }

    return (
        <div className="video-room">
            <div className="video-container">
                {localStream && (
                    <div className="video-item local-video">
                        <VideoPlayer
                            stream={localStream}
                            isMuted={true}
                            username={`${username} (You)`}
                        />
                    </div>
                )}

                {Array.from(remoteStreams.entries()).map(([remoteUserId, stream]) => (
                    <div key={remoteUserId} className="video-item">
                        <VideoPlayer
                            stream={stream}
                            username={`User-${remoteUserId}`}
                        />
                    </div>
                ))}
            </div>

            <div className="sidebar">
                <ParticipantList participants={participants}/>
            </div>

            <Controls
                isAudioEnabled={isAudioEnabled}
                isVideoEnabled={isVideoEnabled}
                isScreenSharing={isScreenSharing}
                onToggleAudio={handleToggleAudio}
                onToggleVideo={handleToggleVideo}
                onToggleScreenShare={handleToggleScreenShare}
                onLeaveRoom={handleLeaveRoom}
            />
        </div>
    );
};

export default VideoRoom;