<template>
  <div class="app-container">
    <!-- 添加留言表单 -->
    <div class="filter-container" style="margin-bottom: 15px">
      <el-input v-model="newMessage" placeholder="请输入留言内容" style="width: 400px;" class="filter-item" />
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-edit" @click="handleCreateMessage">
        提交留言
      </el-button>
    </div>

    <!-- 留言列表 -->
    <el-table
      ref="messageTable"
      :data="messageList"
      border
      style="width: 100%">
      <el-table-column
        prop="userName"
        label="用户名"
        width="120">
      </el-table-column>
      <el-table-column
        prop="content"
        label="留言内容"
        min-width="300"
        show-overflow-tooltip>
      </el-table-column>
      <el-table-column
        prop="createTime"
        label="留言时间"
        width="200"
        :formatter="formatDate">
      </el-table-column>
    </el-table>

    <!-- 分页条 -->
    <el-pagination
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page.sync="queryParam.page"
      :page-sizes="[5, 10, 20, 50]"
      :page-size="queryParam.limit"
      layout="total, sizes, prev, pager, next, jumper"
      :total="recordTotal"
      style="margin-top: 15px">
    </el-pagination>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import waves from '@/directive/waves'
import { fetchMessageList, createMessage } from '@/api/message'

export default {
  name: 'Message',
  directives: { waves },
  computed: {
    // 关键修复：根据 Vuex 中实际的 getter 名称调整（若用户名存放在 name 字段，则改为 'name'）
    ...mapGetters(['id', 'name']) // 假设 Vuex 中用户名为 'name'，而非 'username'
  },
  data() {
    return {
      messageList: [],
      recordTotal: 0,
      queryParam: {
        page: 1,
        limit: 10
      },
      newMessage: ''
    }
  },
  created() {
    this.fetchMessages()
  },
  methods: {
    fetchMessages() {
      fetchMessageList(this.queryParam).then(res => {
        console.log('留言列表数据:', res.data)
        this.messageList = (res.data || []).map(msg => ({
          ...msg,
          userName: msg.userName || '未知用户'
        }))
        this.recordTotal = res.count || 0
      }).catch(error => {
        console.error('获取留言列表失败:', error)
        this.$message.error('获取留言列表失败')
      })
    },

    handleSizeChange(curSize) {
      this.queryParam.limit = curSize
      this.fetchMessages()
    },
    handleCurrentChange(curPage) {
      this.queryParam.page = curPage
      this.fetchMessages()
    },

    // 提交留言（修复用户名获取逻辑）
    handleCreateMessage() {
      if (!this.newMessage.trim()) {
        this.$message.warning('请输入留言内容')
        return
      }
      if (!this.id) {
        this.$message.warning('请先登录')
        return
      }
      // 关键修复：使用 Vuex 中正确的用户名字段（若为 name 则用 this.name）
      const submitData = {
        userId: this.id,
        content: this.newMessage,
        userName: this.name || '匿名用户' // 兜底处理，避免 undefined
      }
      createMessage(submitData).then(res => {
        if (res?.code === 0) {
          this.$message.success('留言提交成功')
          this.newMessage = ''
          this.fetchMessages()
        } else {
          this.$message.error(res?.message || '留言提交失败')
        }
      }).catch(error => {
        console.error('提交留言失败:', error)
        this.$message.error('提交留言失败')
      })
    },

    formatDate(row) {
      const timeStr = row.createTime
      if (!timeStr) return '未知时间'
      return new Date(timeStr).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.filter-container {
  display: flex;
  align-items: center;
  gap: 10px;
}
.el-table {
  margin-top: 10px;
}
</style>
