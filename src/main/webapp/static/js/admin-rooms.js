// 全局变量存储选项数据，方便级联选择
let globalOptions = {
    areas: [],
    buildings: [],
    roomTypes: []
};

document.addEventListener("DOMContentLoaded", () => {
    loadOptions(); // 先加载下拉选项
    loadRooms();   // 再加载列表

    // 监听表单提交
    document.getElementById("roomForm").addEventListener("submit", handleSave);

    // 监听地区选择变化，实现级联更新楼盘
    document.getElementById("areaId").addEventListener("change", (e) => {
        updateBuildingOptions(e.target.value);
    });
});

// --- 1. 加载辅助数据 (Options) ---
async function loadOptions() {
    try {
        const res = await fetch("../api/admin/options");
        if (!res.ok) throw new Error("Failed to load options");
        const data = await res.json();

        globalOptions = data;

        // 填充 Area 下拉框
        const areaSelect = document.getElementById("areaId");
        areaSelect.innerHTML = '<option value="">Select Area</option>';
        data.areas.forEach(a => {
            areaSelect.innerHTML += `<option value="${a.id}">${a.name}</option>`;
        });

        // 填充 RoomType 下拉框
        const typeSelect = document.getElementById("roomTypeId");
        typeSelect.innerHTML = '<option value="">Select Type</option>';
        data.roomTypes.forEach(t => {
            typeSelect.innerHTML += `<option value="${t.id}">${t.name}</option>`;
        });

    } catch (e) {
        console.error(e);
        alert("Error loading form options");
    }
}

// 根据选中的 AreaID 过滤楼盘
function updateBuildingOptions(selectedAreaId) {
    const buildingSelect = document.getElementById("buildingId");
    buildingSelect.innerHTML = '<option value="">Select Building</option>';

    if (!selectedAreaId) return;

    // 过滤属于该地区的楼盘
    const filteredBuildings = globalOptions.buildings.filter(b => b.areaId == selectedAreaId);

    filteredBuildings.forEach(b => {
        buildingSelect.innerHTML += `<option value="${b.id}">${b.name}</option>`;
    });
}

// --- 2. 加载房源列表 (CRUD: Read) ---
async function loadRooms() {
    const tbody = document.getElementById("tableBody");
    try {
        const res = await fetch("../api/admin/rooms");
        if (res.status === 401 || res.status === 403) {
            window.location.href = "../login.jsp";
            return;
        }
        const rooms = await res.json();

        tbody.innerHTML = "";
        if (rooms.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">No rooms found.</td></tr>';
            return;
        }

        rooms.forEach(r => {
            const img = r.coverImage || "https://via.placeholder.com/50";
            const row = `
                <tr>
                    <td>${r.id}</td>
                    <td><img src="${img}" style="width:50px; height:40px; object-fit:cover; border-radius:4px;"></td>
                    <td>${r.title}</td>
                    <td>${r.areaName || '-'}</td>
                    <td>¥${r.price}</td>
                    <td>
                        <i class="fa-solid fa-pen-to-square btn-edit" onclick='openEditModal(${JSON.stringify(r)})'></i>
                        <i class="fa-solid fa-trash btn-delete" onclick="deleteRoom(${r.id})"></i>
                    </td>
                </tr>
            `;
            tbody.innerHTML += row;
        });

    } catch (e) {
        console.error(e);
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center; color:red;">Error loading data</td></tr>';
    }
}

// --- 3. 保存房源 (Create/Update) ---
async function handleSave(e) {
    e.preventDefault();

    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData.entries());
    const roomId = document.getElementById("roomId").value;

    // 如果有 ID，则是更新(PUT)，否则是新增(POST)
    const method = roomId ? "PUT" : "POST";
    const url = "../api/admin/rooms";

    try {
        const res = await fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        if (res.ok) {
            alert("Saved successfully!");
            closeModal();
            loadRooms(); // 刷新列表
        } else {
            const err = await res.text();
            alert("Error: " + err);
        }
    } catch (error) {
        console.error(error);
        alert("Request failed");
    }
}

// --- 4. 删除房源 (Delete) ---
async function deleteRoom(id) {
    if (!confirm("Are you sure you want to delete this room?")) return;

    try {
        const res = await fetch(`../api/admin/rooms?id=${id}`, {
            method: "DELETE"
        });

        if (res.ok) {
            loadRooms();
        } else {
            alert("Delete failed");
        }
    } catch (e) {
        alert("Error deleting");
    }
}

// --- 5. 模态框操作 ---
const modal = document.getElementById("roomModal");
const form = document.getElementById("roomForm");

function openModal() {
    // 重置表单为“新增”状态
    form.reset();
    document.getElementById("roomId").value = "";
    document.getElementById("modalTitle").innerText = "Add New Room";
    // 重新触发一次地区变化以清空楼盘
    updateBuildingOptions("");

    modal.style.display = "block";
}

function openEditModal(room) {
    // 填充表单为“编辑”状态
    document.getElementById("roomId").value = room.id;
    document.getElementById("title").value = room.title;
    document.getElementById("price").value = room.price;
    document.getElementById("size").value = room.size;
    document.getElementById("description").value = room.description || "";
    document.getElementById("coverImage").value = room.coverImage || "";

    // 这里的 room 对象可能没有 areaId (取决于后端 DTO 怎么传)，
    // 如果后端传的是 areaName，我们可能需要在前端想办法匹配 ID，
    // 或者最好后端 Room 实体里包含 areaId 字段（之前 RoomDao 查出来的实体应该有 getArea().getId()）
    // 为了简化，我们假设后端 JSON 序列化时包含了 areaId 等 ID 信息。
    // 如果没有，你需要修改 RoomDao 或 Gson Adapter 来暴露这些 ID。

    // 尝试直接设置 Select 值 (如果后端返回了关联对象的完整结构)
    if (room.area && room.area.id) {
        document.getElementById("areaId").value = room.area.id;
        updateBuildingOptions(room.area.id); // 先刷新楼盘列表

        // 稍等一下再设楼盘，或直接设
        if (room.building && room.building.id) {
            document.getElementById("buildingId").value = room.building.id;
        }
    }

    if (room.roomType && room.roomType.id) {
        document.getElementById("roomTypeId").value = room.roomType.id;
    }

    document.getElementById("modalTitle").innerText = "Edit Room";
    modal.style.display = "block";
}

function closeModal() {
    modal.style.display = "none";
}

// 点击遮罩层关闭
window.onclick = function(event) {
    if (event.target == modal) {
        closeModal();
    }
}