import React from 'react';
import { 
  HandThumbsUpFill, HeartFill, EmojiLaughingFill, 
  EmojiWinkFill, EmojiFrownFill, EmojiAngryFill, ChatLeftText 
} from 'react-bootstrap-icons';

const ReactionsDisplay = ({ reactions = [{}], totalComments = 0 }) => {
  const r = reactions[0] || {};

  return (
    <div className="mb-2 text-muted d-flex gap-3 align-items-center">
      {r.like > 0 && (
        <>
          <HandThumbsUpFill className="text-primary" /> {r.like}
        </>
      )}
      {r.love > 0 && (
        <>
          <HeartFill className="text-danger" /> {r.love}
        </>
      )}
      {r.haha > 0 && (
        <>
          <EmojiLaughingFill className="text-warning" /> {r.haha}
        </>
      )}
      {r.wow > 0 && (
        <>
          <EmojiWinkFill className="text-info" /> {r.wow}
        </>
      )}
      {r.sad > 0 && (
        <>
          <EmojiFrownFill className="text-secondary" /> {r.sad}
        </>
      )}
      {r.angry > 0 && (
        <>
          <EmojiAngryFill className="text-danger" /> {r.angry}
        </>
      )}
      {totalComments > 0 && (
        <>
          <ChatLeftText className="text-muted" /> {totalComments}
        </>
      )}
    </div>
  );
};

export default ReactionsDisplay;
