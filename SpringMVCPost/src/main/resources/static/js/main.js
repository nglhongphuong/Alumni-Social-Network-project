
function deletePost(endpoint, id){
    if(confirm("Bạn có muốn xóa?") === true){
            fetch(`${endpoint}/${id}`,{
        method: "delete"
    }).then(res => {
        if(res.status === 204) {
            alert("Delete Successful!");
            location.reload();
        } else{
            alert("Error system!");
        }
    });
    }
}