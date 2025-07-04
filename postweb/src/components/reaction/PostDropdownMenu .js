
import { Dropdown } from 'react-bootstrap';
import { ThreeDotsVertical } from 'react-bootstrap-icons';

const PostDropdownMenu = ({ post, handleDeletePost, handleTogglePostAttribute,handleEditClick  }) => {
  return (
    <Dropdown align="end">
      <Dropdown.Toggle
        variant="link"
        bsPrefix="p-0 border-0 bg-transparent"
        id={`dropdown-${post.postId}`}
      >
        <ThreeDotsVertical size={20} />
      </Dropdown.Toggle>

      <Dropdown.Menu>
        <Dropdown.Item onClick={() => handleDeletePost(post.postId)}>
          Xóa bài viết
        </Dropdown.Item>

        {post.isCommentLocked ? (
          <Dropdown.Item onClick={() => handleTogglePostAttribute(post.postId, 'isCommentLocked', false)}>
            Mở bình luận
          </Dropdown.Item>
        ) : (
          <Dropdown.Item onClick={() => handleTogglePostAttribute(post.postId, 'isCommentLocked', true)}>
            Khóa bình luận
          </Dropdown.Item>
        )}
{/* 
        {post.visibility === 'public' ? (
          <Dropdown.Item onClick={() => handleTogglePostAttribute(post.postId, 'visibility', 'private')}>
            Chuyển sang riêng tư
          </Dropdown.Item>
        ) : (
          <Dropdown.Item onClick={() => handleTogglePostAttribute(post.postId, 'visibility', 'public')}>
            Chuyển sang công khai
          </Dropdown.Item>
        )} */}

        <Dropdown.Item onClick={() => handleEditClick(post)}>
          Chỉnh sửa bài viết
        </Dropdown.Item>

      </Dropdown.Menu>
    </Dropdown>
  );
};

export default PostDropdownMenu;
