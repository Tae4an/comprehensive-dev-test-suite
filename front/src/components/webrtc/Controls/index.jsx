import React from 'react';
import { 
    Mic, MicOff, 
    Videocam, VideocamOff,
    ScreenShare, StopScreenShare,
    CallEnd
} from '@mui/icons-material';
import './style.css';

const Controls = ({
    isAudioEnabled,
    isVideoEnabled,
    isScreenSharing,
    onToggleAudio,
    onToggleVideo,
    onToggleScreenShare,
    onLeaveRoom
}) => {
    return (
        <div className="controls-container">
            <button 
                className={`control-button ${!isAudioEnabled ? 'disabled' : ''}`}
                onClick={onToggleAudio}
                title={isAudioEnabled ? 'Mute' : 'Unmute'}
            >
                {isAudioEnabled ? <Mic /> : <MicOff />}
            </button>

            <button 
                className={`control-button ${!isVideoEnabled ? 'disabled' : ''}`}
                onClick={onToggleVideo}
                title={isVideoEnabled ? 'Turn off camera' : 'Turn on camera'}
            >
                {isVideoEnabled ? <Videocam /> : <VideocamOff />}
            </button>

            <button 
                className={`control-button ${isScreenSharing ? 'active' : ''}`}
                onClick={onToggleScreenShare}
                title={isScreenSharing ? 'Stop sharing' : 'Share screen'}
            >
                {isScreenSharing ? <StopScreenShare /> : <ScreenShare />}
            </button>

            <button 
                className="control-button end-call"
                onClick={onLeaveRoom}
                title="Leave room"
            >
                <CallEnd />
            </button>
        </div>
    );
};

export default Controls;