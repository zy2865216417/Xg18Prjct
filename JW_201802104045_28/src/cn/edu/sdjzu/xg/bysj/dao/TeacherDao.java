package cn.edu.sdjzu.xg.bysj.dao;

import cn.edu.sdjzu.xg.bysj.domain.Teacher;
import cn.edu.sdjzu.xg.bysj.service.DegreeService;
import cn.edu.sdjzu.xg.bysj.service.DepartmentService;
import cn.edu.sdjzu.xg.bysj.service.ProfTitleService;
import cn.edu.sdjzu.xg.bysj.service.TeacherService;
import util.JdbcHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public final class TeacherDao {
	private static TeacherDao teacherDao=new TeacherDao();
	private TeacherDao(){}
	public static TeacherDao getInstance(){
		return teacherDao;
	}
	public Set<Teacher> findAll() throws SQLException,ClassNotFoundException{
		Set<Teacher> teachers = new HashSet<Teacher>();
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//在该连接上创建语句盒子对象
		PreparedStatement preparedStatement = connection.prepareStatement("select * from teacher");
		ResultSet resultSet = preparedStatement.executeQuery();
		//若结果集仍然有下一条记录，则执行循环体
		while (resultSet.next()){
			teachers.add(new Teacher(resultSet.getInt("id"),
					resultSet.getString("name"),
					ProfTitleService.getInstance().find(resultSet.getInt("profTitle_id")),
					DegreeService.getInstance().find(resultSet.getInt("degree_id")),
					DepartmentService.getInstance().find(resultSet.getInt("department_id"))
			));
		}
		//执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
		JdbcHelper.close(preparedStatement,connection);
		return teachers;
	}
	
	public Teacher find(Integer id) throws SQLException,ClassNotFoundException{
		Teacher teacher = null;
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//在该连接上创建语句盒子对象
		PreparedStatement preparedStatement = connection.prepareStatement("select * from teacher where id=?");
		preparedStatement.setInt(1,id);
		ResultSet resultSet = preparedStatement.executeQuery();
		//若结果集仍然有下一条记录，则执行循环体
		if(resultSet.next()){
			teacher = new Teacher(resultSet.getInt("id"),
					resultSet.getString("name"),
					ProfTitleService.getInstance().find(resultSet.getInt("profTitle_id")),
					DegreeService.getInstance().find(resultSet.getInt("degree_id")),
					DepartmentService.getInstance().find(resultSet.getInt("department_id"))
			);
		}
		//执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
		JdbcHelper.close(preparedStatement,connection);
		return teacher;
	}
	
	public boolean update(Teacher teacher) throws SQLException{
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//在该连接上创建语句盒子对象
		PreparedStatement preparedStatement = connection.prepareStatement(
				"update teacher set name=?,proftitle_id=?,degree_id=?,department_id=? where id=?");
		//为预编译语句参数赋值
		preparedStatement.setString(1,teacher.getName());
		preparedStatement.setInt(2,teacher.getTitle().getId());
		preparedStatement.setInt(3,teacher.getDegree().getId());
		preparedStatement.setInt(4,teacher.getDepartment().getId());
		preparedStatement.setInt(5,teacher.getId());
		//执行预编译语句，用其返回值、影响的行为数为赋值affectedRowNum
		int affectedRowNum = preparedStatement.executeUpdate();
		JdbcHelper.close(preparedStatement,connection);
		return affectedRowNum >0;
	}
	
	public boolean add(Teacher teacher) throws SQLException,ClassNotFoundException{
		//获得连接对象
		Connection connection = JdbcHelper.getConn();
		//根据连接对象准备语句对象，如果sql语句为多行，注意语句不同部分之间要有空格
		PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO teacher" +
				"(name,proftitle_id,degree_id,department_id)" +
				" VALUES(?,?,?,?)");
		//为SQL语句赋值
		preparedStatement.setString(1,teacher.getName());
		preparedStatement.setInt(2,teacher.getTitle().getId());
		preparedStatement.setInt(3,teacher.getDegree().getId());
		preparedStatement.setInt(4,teacher.getDepartment().getId());
		//执行SQL语句
		int affectedRows = preparedStatement.executeUpdate();
		//关闭资源
		JdbcHelper.close(preparedStatement,connection);
		return affectedRows>0;
	}

	public boolean delete(Integer id) throws SQLException,ClassNotFoundException{
		//获取数据库连接对象
		Connection connection = JdbcHelper.getConn();
		//创建sql语句
		String teacher_sql = "DELETE FROM teacher" + " WHERE id=?";
		//在该连接上创建预编译语句对象
		PreparedStatement preparedStatement = connection.prepareStatement(teacher_sql);
		//为预编译语句赋值
		preparedStatement.setInt(1,id);
		int affectedRows = preparedStatement.executeUpdate();
		JdbcHelper.close(preparedStatement,connection);
		return affectedRows>0;
	}
	
	public boolean delete(Teacher teacher) throws SQLException,ClassNotFoundException{
		return delete(teacher.getId());
	}

	public static void main(String[] args) throws SQLException,ClassNotFoundException{
		Teacher teacher1 = TeacherService.getInstance().find(3);
		//输出原来的信息
		System.out.println(teacher1.getName());
		//修改
		teacher1.setName("王红");
		//更新
		TeacherDao.getInstance().update(teacher1);
		//找到更新的对象
		Teacher teacher2 = TeacherService.getInstance().find(3);
		//输出更新对象的name
		System.out.println(teacher2.getName());
	}
}
