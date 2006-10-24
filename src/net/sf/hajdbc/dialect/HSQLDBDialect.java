/*
 * HA-JDBC: High-Availability JDBC
 * Copyright (c) 2004-2006 Paul Ferraro
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by the 
 * Free Software Foundation; either version 2.1 of the License, or (at your 
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License 
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, 
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Contact: ferraro@users.sourceforge.net
 */
package net.sf.hajdbc.dialect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Dialect for <a href="http://www.hsqldb.org">HSQLDB</a>.
 * 
 * @author  Paul Ferraro
 * @since   1.1
 */
public class HSQLDBDialect extends DefaultDialect
{
	/**
	 * @see net.sf.hajdbc.dialect.DefaultDialect#executeFunctionKeyword()
	 */
	@Override
	protected String executeFunctionFormat()
	{
		return "CALL {0}";
	}

	/**
	 * @see net.sf.hajdbc.dialect.DefaultDialect#getSequences(java.sql.Connection)
	 */
	@Override
	public Collection<String> getSequences(Connection connection) throws SQLException
	{
		List<String> sequenceList = new LinkedList<String>();
		
		Statement statement = connection.createStatement();
		
		ResultSet resultSet = statement.executeQuery("SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SYSTEM_SEQUENCES");
		
		while (resultSet.next())
		{
			sequenceList.add(resultSet.getString(1));
		}

		resultSet.close();
		statement.close();
		
		return sequenceList;
	}

	/**
	 * @see net.sf.hajdbc.dialect.DefaultDialect#getCurrentSequenceValueSQL(java.lang.String)
	 */
	@Override
	public String getCurrentSequenceValueSQL(String sequence)
	{
		return "SELECT NEXT_VALUE - 1 FROM INFORMATION_SCHEMA.SYSTEM_SEQUENCES WHERE SEQUENCE_NAME = '" + sequence + "'";
	}

	/**
	 * Deferrability clause is not supported.
	 * @see net.sf.hajdbc.dialect.DefaultDialect#createForeignKeyConstraintFormat()
	 */
	@Override
	protected String createForeignKeyConstraintFormat()
	{
		return "ALTER TABLE {1} ADD CONSTRAINT {0} FOREIGN KEY ({2}) REFERENCES {3} ({4}) ON DELETE {5,choice,0#CASCADE|1#RESTRICT|2#SET NULL|3#NO ACTION|4#SET DEFAULT} ON UPDATE {6,choice,0#CASCADE|1#RESTRICT|2#SET NULL|3#NO ACTION|4#SET DEFAULT}";
	}
}
